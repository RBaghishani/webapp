package it.unipd.webapp.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import it.unipd.webapp.entity.User;
import it.unipd.webapp.repository.CredentialRepository;
import it.unipd.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MfaService {
    private final GoogleAuthenticator gAuth;
    private final CredentialRepository credentialRepository;

    private final UserRepository userRepository;
    long timeWindow = 3;

    @Autowired
    public MfaService(GoogleAuthenticator gAuth, CredentialRepository credentialRepository, UserRepository userRepository) {
        this.gAuth = gAuth;
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
    }

    public BitMatrix getQrCodeBitMatrix(String email) throws WriterException {
        String otpAuthURL = getOtpAuthURL(email);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
    }

    public String getOtpAuthURL(String email) {
        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("my-secure-project", email, key);
        return otpAuthURL;
    }

    public boolean authorizeUser(String email, int code) {
        boolean auth = gAuth.authorizeUser(email, code);
        //todo make it better! not calling the db each time
        if (auth) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow();
            user.setMfaEnable(true);
            userRepository.save(user);
        }
        return auth;
    }
}
