package com.playgrounds;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class URAPasswordSignature extends AbstractMediator {

    private String uraCertificatePath;
    private String privateKeyPath;
    private String keystorePassword;
    private String alias;

    private String password;
    private String dataForSigning;

    @Override
    public boolean mediate(MessageContext context) {
        try {
            // Encrypt Password
            if (password != null && !password.isEmpty() && uraCertificatePath != null && !uraCertificatePath.isEmpty()) {
                String encryptedPassword = encrypt(password, uraCertificatePath);
                System.out.println("Encrypted Password: " + encryptedPassword);
                context.setProperty("EncryptedPassword", encryptedPassword);
            }

            // Generate Signature
            if (dataForSigning != null && !dataForSigning.isEmpty() && privateKeyPath != null && !privateKeyPath.isEmpty() && alias != null && !alias.isEmpty() && keystorePassword != null) {
                String signature = generateSignature(dataForSigning, privateKeyPath, alias, keystorePassword);
                System.out.println("Signature: " + signature);
                context.setProperty("Signature", signature);
            }

        } catch (Exception e) {
            System.out.println("Error processing: " + e.getMessage());
            log.error("Error processing: " + e.getMessage(), e);
        }
        return true;
    }

    public String getUraCertificatePath() {
        return uraCertificatePath;
    }

    public void setUraCertificatePath(String uraCertificatePath) {
        this.uraCertificatePath = uraCertificatePath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getDataForSigning() {
        return dataForSigning;
    }

    public void setDataForSigning(String dataForSigning) {
        this.dataForSigning = dataForSigning;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static String encrypt(String plainText, String certificatePath) throws Exception {
        PublicKey publicKey;
        try (InputStream inStream = new FileInputStream(certificatePath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
            publicKey = cert.getPublicKey();
        }
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String generateSignature(String data, String keystorePath, String alias, String keystorePassword) throws Exception {
        Signature signature;
        PrivateKey privateKey;
        try (FileInputStream is = new FileInputStream(keystorePath)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(is, keystorePassword.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
        }
        signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }
}
