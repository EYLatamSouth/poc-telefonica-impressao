package br.com.telefonica.impressao.service;

import br.com.telefonica.impressao.message.ProducerImpressao;
import br.com.telefonica.impressao.model.ImpressaoMongoDB;
import br.com.telefonica.impressao.repository.ImpressaoRepository;
import br.com.telefonica.impressao.util.Functions;
import br.com.telefonica.impressao.util.SftpClient;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Component("processBillings")
public class ImpressaoBillings {

    @Value("${config.sftp.server}")
    private String sftpServer;

    @Value("${config.sftp.sendUser}")
    private String sendUser;

    @Value("${config.sftp.returnUser}")
    private String returnUser;

    @Value("${config.sftp.password}")
    private String sftpPass;

    @Value("${directory.received}")
    private String receivedFilesDirectory;

    @Value("${directory.sent}")
    private String sentFilesDirectory;

    @Value("${directory.processed}")
    private String processedFilesDirectory;

    @Autowired
    private ImpressaoRepository cobrancaMongoRepository;

    @Autowired
    private ProducerImpressao producerImpressao;

    public void readBillings(){
        System.out.println("Starting reading FTP Server");
        SftpClient sftpClient = new SftpClient(sftpServer, sendUser);

        try {
            System.out.println("Connecting");
            sftpClient.authPassword(sftpPass);
            System.out.println("Listing Files on SFT Server root folder /");
            List<String> fileNames = sftpClient.listFiles("/");
            File folder = new File(receivedFilesDirectory);
            if(!folder.exists()){
                folder.mkdir();
            }
            for (String fileName: fileNames) {
                sftpClient.downloadFile(fileName,receivedFilesDirectory+fileName);
            }
        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        } finally {
            sftpClient.close();
        }

    }

    public void sendBillings(){
        SftpClient sftpClient = new SftpClient(sftpServer, returnUser);
        try {
            String billingStatus = "Fatura paga em "+new Date();
            String encryptedStatus = Functions.encrypt(billingStatus);
            String fileName = String.format("billing%1s.txt",System.currentTimeMillis());
            File sentFolder = new File(sentFilesDirectory);
            if(!sentFolder.exists()){
                sentFolder.mkdir();
            }
            FileOutputStream billingOutPut = new FileOutputStream(sentFilesDirectory+fileName);
            billingOutPut.write(encryptedStatus.getBytes());
            System.out.println("Connecting on SFTP Server");
            sftpClient.authPassword(sftpPass);
            sftpClient.uploadFile(sentFilesDirectory+fileName, fileName);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            sftpClient.close();
        }

    }

    public void uploadBillings(ImpressaoMongoDB billing){
        SftpClient sftpClient = new SftpClient(sftpServer, sendUser);
        try {
            String billingStatus = String.format("BillingID=%1$s Vencimento=%2$s Valor=%3$s", billing.getBillingId(),
                    billing.getBillingVencimento(), billing.getBillingValorFatura());
            System.out.println("------ "+billingStatus);
            String encryptedStatus = Functions.encrypt(billingStatus);
            System.out.println("------ "+encryptedStatus);
            String fileName = String.format("billing_%1s.txt", billing.getBillingId());
            File sentFolder = new File(sentFilesDirectory);
            if(!sentFolder.exists()){
                sentFolder.mkdir();
            }
            FileOutputStream billingOutPut = new FileOutputStream(sentFilesDirectory+fileName);
            billingOutPut.write(encryptedStatus.getBytes());
            System.out.println("Connecting on SFTP Server");
            sftpClient.authPassword(sftpPass);
            sftpClient.uploadFile(sentFilesDirectory+fileName, fileName);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            sftpClient.close();
        }
    }

    public void searchStatusThanUpload(){

        List<ImpressaoMongoDB> listaStatusNOK = cobrancaMongoRepository.findByBillingStatus("1");

        listaStatusNOK.forEach(status -> {
            try {
                uploadBillings(status);
                status.setBellingStatus("0");
            }catch (Exception e){
                status.setBellingStatus("1");
            }finally {
                cobrancaMongoRepository.save(status);
            }
        });

    }

    public void decryptAndUpdateMongo() throws Exception {

        try {
            File folder = new File(receivedFilesDirectory);
            for (File x : folder.listFiles()){

                Scanner sc = new Scanner(x);
                String line = sc.nextLine();

                String DecriptedLine = Functions.decrypt(line);

                String[] split = DecriptedLine.split(";");
                String idstring = split[8];
                Long id = Long.parseLong(idstring);

                ImpressaoMongoDB billing = cobrancaMongoRepository.findByBilling_id(id);
                if (billing != null){
                    String status = split[11];
                    billing.setBellingStatus(status);
                    cobrancaMongoRepository.save(billing);
                    producerImpressao.send(billing);
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void processesServiceOk(){
        Functions.processesOkAndCopyToPath(receivedFilesDirectory, processedFilesDirectory);
    }

    public void processesServiceNOk(){
        Functions.processesNOkAndCopyToPath(receivedFilesDirectory, processedFilesDirectory);
    }
}
