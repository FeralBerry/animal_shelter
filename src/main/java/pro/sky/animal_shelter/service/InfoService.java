package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.Info;
import pro.sky.animal_shelter.model.InfoRepository;

@Slf4j
@Service
public class InfoService {
    @Autowired
    private InfoRepository infoRepository;
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
//- Бот может выдать список животных для усыновления (возможность перейти). /pet-list
    //- Бот может выдать правила знакомства с животным до того, как забрать его из приюта.
    //- Бот может выдать список документов, необходимых для того, чтобы взять животное из приюта.
    //- Бот может выдать список рекомендаций по транспортировке животного.
    //- Бот может выдать список рекомендаций по обустройству дома для щенка.
    //- Бот может выдать список рекомендаций по обустройству дома для взрослого животного.
    //- Бот может выдать список рекомендаций по обустройству дома для животного с ограниченными возможностями (зрение, передвижение).
    //- Бот может выдать советы кинолога по первичному общению с собакой*.*
    //- Бот может выдать рекомендации по проверенным кинологам для дальнейшего обращения к ним*.*
    //- Бот может выдать список причин, почему могут отказать и не дать забрать собаку из приюта.
}
