package pro.sky.animal_shelter.controller;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.service.CallService;

@Service
public class CallController {
    /**
     *
     */
    private final CallService callService;

    /**
     *
     * @param callService
     */
    public CallController(CallService callService){
        this.callService = callService;
    }

    /**
     *
     * @param chatId
     * @return
     */
    public long sendMessageToChat(long chatId){
        return callService.sendMessageChat(chatId);
    }
}
