package it.unipd.webapp.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import it.unipd.webapp.model.EmailDto;
import it.unipd.webapp.repository.CredentialRepository;
import it.unipd.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MfaService {
    private final GoogleAuthenticator gAuth;
    private final CredentialRepository credentialRepository;
    long timeWindow = 3;

    @Autowired
    public MfaService(GoogleAuthenticator gAuth, CredentialRepository credentialRepository) {
        this.gAuth = gAuth;
        this.credentialRepository = credentialRepository;
    }

    public BitMatrix getQrCodeBitMatrix(String email) throws WriterException {
        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("my-secure-project", email, key);
        return qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
    }

    public boolean authorizeUser(String email, int code) {
        return gAuth.authorizeUser(email, code, timeWindow);
    }
}
