package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.Info;
import pro.sky.animal_shelter.model.InfoRepository;

@Slf4j
@Service
public class InfoService {
    /**
     *
     */
    private final InfoRepository infoRepository;

    /**
     *
     * @param infoRepository
     */
    public InfoService(InfoRepository infoRepository){
        this.infoRepository = infoRepository;
    }

    /**
     *
     * @return
     */
    public String info(){
        StringBuilder message = new StringBuilder();
        var info = infoRepository.findAll();
        if(info.isEmpty()){
            message.append("Описание пока отсутствует");
        } else {
            for (Info info1 : info) {

            }
        }
        return message.toString();
    }
}
