package it.unipd.webapp.controller;


import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import it.unipd.webapp.model.ValidateCodeDto;
import it.unipd.webapp.model.Validation;
import it.unipd.webapp.model.dataDto;
import it.unipd.webapp.service.MfaService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.PUT,RequestMethod.DELETE,RequestMethod.OPTIONS,RequestMethod.HEAD,RequestMethod.GET,RequestMethod.POST,RequestMethod.PATCH})
@RequiredArgsConstructor
@RequestMapping("/api/v1/mfa")
@PermitAll
public class MfaController {

    @Autowired
    private final MfaService mfaService;


   /* @SneakyThrows
    @GetMapping("/generate")
    public void generate(
            @RequestParam(name = "email", required = false) String email,
            HttpServletResponse response) {
        BitMatrix bitMatrix = mfaService.getQrCodeBitMatrix(email);
        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.close();
    }*/

    @SneakyThrows
    @GetMapping("/generate")
    public ResponseEntity<dataDto> generate(
            @Valid @Email @RequestParam(name = "email", required = false) String email) {
        return ResponseEntity.ok(new dataDto(mfaService.getOtpAuthURL(email)));
    }

    @PostMapping("/validate")
    public ResponseEntity<Validation> validateKey(@Valid @RequestBody ValidateCodeDto body) {
        return ResponseEntity.ok(new Validation(mfaService.authorizeUser(body.getEmail(), body.getCode())));
    }

    /*@GetMapping("/scratches")
    public List<Integer> getScratches(@RequestParam EmailDto generateCodeDto) {
        return getScratchCodes(generateCodeDto.getEmail());
    }

    private List<Integer> getScratchCodes(@PathVariable String username) {
        return credentialRepository.getUser(username).getScratchCodes();
    }

    @PostMapping("/scratches/")
    public Validation validateScratch(@RequestBody ValidateCodeDto body) {
        List<Integer> scratchCodes = getScratchCodes(body.getEmail());
        Validation validation = new Validation(scratchCodes.contains(body.getCode()));
        scratchCodes.remove(body.getCode());
        return validation;
    }*/
}