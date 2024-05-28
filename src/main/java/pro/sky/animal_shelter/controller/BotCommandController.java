package pro.sky.animal_shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.animal_shelter.exeptions.NoSuchAboutException;
import pro.sky.animal_shelter.exeptions.NoSuchInfoException;
import pro.sky.animal_shelter.model.About;
import pro.sky.animal_shelter.model.Info;
import pro.sky.animal_shelter.service.*;

@Tag(name = "Контроллер команд бота", description = "Обработка всех команд бота начинающихся с /")
@RestController
@RequestMapping("api/url")
public class BotCommandController {
    private final StartService startService;
    private final InfoService infoService;
    private final AboutService aboutService;
    private final PetService petService;
    private final ContactInformationService contactInformationService;

    public BotCommandController(StartService startService, InfoService infoService, AboutService aboutService, PetService petService, ContactInformationService contactInformationService) {
        this.startService = startService;
        this.infoService = infoService;
        this.aboutService = aboutService;
        this.petService = petService;
        this.contactInformationService = contactInformationService;
    }

    @Operation(summary = "Возвращает стартовое сообщение")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Стартовое сообщение найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json"
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Стартовое сообщение не найдено",
                    content =
                    @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @GetMapping("/start")
    public ResponseEntity<String> getStartMessage() {
        String start = startService.start();
        return new ResponseEntity<>(start, HttpStatus.OK);
    }
    @Operation(summary = "Возвращает сообщение со справочной информацией")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Стартовое сообщение со справочной информацией найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Info.class))
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Стартовое сообщение со справочной информацией не найдено",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoSuchInfoException.class)
                    )
            )
    })
    @GetMapping("/info")
    public ResponseEntity<String> getInfoMessage() {
        String info = infoService.info();
        return new ResponseEntity<>(info, HttpStatus.OK);
    }
    @Operation(summary = "Возвращает сообщение c информацией о приюте")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Стартовое сообщение c информацией о приюте найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = About.class))
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Стартовое сообщение c информацией о приюте не найдено",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoSuchAboutException.class)
                    )
            )
    })
    @GetMapping("/about")
    public ResponseEntity<String> getAboutMessage() {
        String about = aboutService.about();
        return new ResponseEntity<>(about, HttpStatus.OK);
    }
    @Operation(summary = "Возвращает сообщение c формой отправки отчета")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Стартовое сообщение c формой отправки отчета найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json"
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Стартовое сообщение c формой отправки отчета не найдено",
                    content =
                    @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @GetMapping("/pet-form")
    public ResponseEntity<String> getPetFormMessage() {
        String petForm = petService.getPetForm();
        return new ResponseEntity<>(petForm, HttpStatus.OK);
    }
    @Operation(summary = "Возвращает сообщение c формой отправки контактной информации")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Стартовое сообщение c формой отправки контактной информации о приюте найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json"
                            )
                    }

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Стартовое сообщение c формой отправки контактной информации не найдено",
                    content =
                    @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @GetMapping("/contact_information")
    public ResponseEntity<String> getContactInformationMessage() {
        String contactInformation = contactInformationService.getContactInformation();
        return new ResponseEntity<>(contactInformation, HttpStatus.OK);
    }
}
