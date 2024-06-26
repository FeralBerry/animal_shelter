package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.Info;
import pro.sky.animal_shelter.model.Repositories.InfoRepository;

@Slf4j
@Service
public class InfoService {

    /**
     * 1. Создаёт экземпляр класса StringBuilder для формирования строки с информацией о данных объекта Info.
     * 2. Находит все объекты типа Info с помощью метода findAll().
     * 3. Если список объектов Info пуст, то в строку добавляется сообщение «Описание пока отсутствует».
     * 4. В противном случае происходит перебор всех найденных объектов и добавление их свойств в формируемую строку.
     * 5. Возвращает сформированную строку как результат работы метода info().
    */
    private final InfoRepository infoRepository;
    public InfoService(InfoRepository infoRepository){
        this.infoRepository = infoRepository;
    }
     
    public String info(){
        StringBuilder message = new StringBuilder();
        var info = infoRepository.findAll();
        if(info.isEmpty()){
            message.append("Описание пока отсутствует")
                    .append("\n");
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
    public void addRules(String text){
        Info info = new Info();
        if(!infoRepository.findAll().isEmpty()){
            info = infoRepository.findInfo();
        }
        info.setRules(text);
        infoRepository.save(info);
    }
    public void addDocuments(String text){
        Info info = new Info();
        if(!infoRepository.findAll().isEmpty()){
            info = infoRepository.findInfo();
        }
        info.setDocuments(text);
        infoRepository.save(info);
    }
    public void addTransportation(String text){
        Info info = new Info();
        if(!infoRepository.findAll().isEmpty()){
            info = infoRepository.findInfo();
        }
        info.setTransportation(text);
        infoRepository.save(info);
    }
}
