package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.service.api.CompilationService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> saveCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compilationService.addCompilation(newCompilationDto));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable long compId) {
        compilationService.deleteCompilation(compId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest updateRequest) {
        return ResponseEntity.ok(
                compilationService.updateCompilation(compId, updateRequest));
    }
}
