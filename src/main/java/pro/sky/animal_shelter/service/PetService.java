package pro.sky.animal_shelter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.model.PetRepository;

@Slf4j
@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;
    // получать информацию из БД всех животных
    public String getPets(){
        return "";
    }
    // получить информацию по определенному животному
    public String getPet(){
        return "";
    }
    // проверять роль пользователя и добавлять животного в БД
    public String addPet(){
        return "";
    }
}
