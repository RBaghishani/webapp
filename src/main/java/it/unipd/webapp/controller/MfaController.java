package it.unipd.webapp.controller;


import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import it.unipd.webapp.model.EmailDto;
import it.unipd.webapp.model.ValidateCodeDto;
import it.unipd.webapp.model.Validation;
import it.unipd.webapp.service.MfaService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/mfa")
public class MfaController {

    @Autowired
    private final MfaService mfaService;


    @SneakyThrows
    @GetMapping("/generate")
    public void generate(@RequestBody EmailDto generateCodeDto, HttpServletResponse response) {
        BitMatrix bitMatrix = mfaService.getQrCodeBitMatrix(generateCodeDto.getEmail());
        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.close();
    }



    @PostMapping("/validate")
    public Validation validateKey(@RequestBody ValidateCodeDto body) {
        return new Validation(mfaService.authorizeUser(body.getEmail(), body.getCode()));
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