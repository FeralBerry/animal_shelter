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

    /**
     * 1. Создаёт экземпляр класса StringBuilder для формирования строки с информацией о данных объекта Info.
     * 2. Находит все объекты типа Info с помощью метода findAll().
     * 3. Если список объектов Info пуст, то в строку добавляется сообщение «Описание пока отсутствует».
     * 4. В противном случае происходит перебор всех найденных объектов и добавление их свойств в формируемую строку.
     * 5. Возвращает сформированную строку как результат работы метода info().
     */
    public String info(){
        StringBuilder message = new StringBuilder();
        var info = infoRepository.findAll();
        if(info.isEmpty()){
            message.append("Описание пока отсутствует");
        } else {
            for (Info info1 : info) {
                message.append(info1.getDocuments())
                        .append(" ")
                        .append(info1.getRules())
                        .append(" ")
                        .append(info1.getTransportation())
                        .append(" ")
                        .append(info1.getHouseForAPuppy())
                        .append(" ")
                        .append(info1.getHomeForAnAdultAnimal())
                        .append(" ")
                        .append(info1.getHomeForAnAnimalWithDisabilities())
                        .append(" ")
                        .append(info1.getTipsFromADogHandler())
                        .append(" ")
                        .append(info1.getRecommendationsOfADogHandler())
                        .append(" ")
                        .append(info1.getReasonsForRefusal());

            }
        }
        return message.toString();
    }
//- Бот может выдать список животных для усыновления (возможность перейти). /pet_list
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
