package pro.sky.animal_shelter.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.animal_shelter.model.Adoption;
import pro.sky.animal_shelter.model.Report;
import pro.sky.animal_shelter.model.Repositories.AdoptionRepository;
import pro.sky.animal_shelter.model.Repositories.ReportRepository;
import pro.sky.animal_shelter.model.Repositories.UserRepository;
import pro.sky.animal_shelter.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class AdminReportControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ReportRepository reportRepository;
    @MockBean
    AdoptionRepository adoptionRepository;
    @MockBean
    UserRepository userRepository;
    @Test
    void getStartMessage() throws Exception {
        JSONObject jsonObject = new JSONObject();
        Long id = 1L;
        String text = "text";
        Long chatId = 1L;
        Long updatedAt = 1L;
        Boolean checked = false;
        Boolean looked = false;
        jsonObject.put("id",id);
        jsonObject.put("text",text);
        jsonObject.put("chatId",chatId);
        jsonObject.put("updatedAt",updatedAt);
        jsonObject.put("checked",checked);
        jsonObject.put("looked",looked);
        List<Report> reportList = new ArrayList<>();
        Report report = new Report();
        report.setChatId(id);
        report.setText(text);
        report.setChatId(chatId);
        report.setUpdatedAt(updatedAt);
        report.setChecked(checked);
        report.setLooked(looked);
        reportList.add(report);
        doReturn(reportList)
                .when(reportRepository)
                .findReportIsNotCheck();
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/raw-reports")
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value(text))
                .andExpect(jsonPath("$[0].chatId").value(chatId))
                .andExpect(jsonPath("$[0].updatedAt").value(updatedAt))
                .andExpect(jsonPath("$[0].checked").value(checked))
                .andExpect(jsonPath("$[0].looked").value(looked));
    }

    @Test
    void getReportById() throws Exception {
        JSONObject jsonObject = new JSONObject();
        Long id = 1L;
        String text = "text";
        Long chatId = 1L;
        Long updatedAt = 1L;
        Boolean checked = false;
        Boolean looked = false;
        jsonObject.put("id",id);
        jsonObject.put("text",text);
        jsonObject.put("chatId",chatId);
        jsonObject.put("updatedAt",updatedAt);
        jsonObject.put("checked",checked);
        jsonObject.put("looked",looked);
        Report report = new Report();
        report.setChatId(id);
        report.setText(text);
        report.setChatId(chatId);
        report.setUpdatedAt(updatedAt);
        report.setChecked(checked);
        report.setLooked(looked);
        doReturn(Optional.of(report))
                .when(reportRepository)
                .findById(1L);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/report/" + id)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.chatId").value(chatId))
                .andExpect(jsonPath("$.updatedAt").value(updatedAt))
                .andExpect(jsonPath("$.checked").value(checked))
                .andExpect(jsonPath("$.looked").value(looked));
    }
    @Test
    void checkReportById() throws Exception {
        JSONObject jsonObject = (JSONObject) testObj().get(0);
        Report report = (Report) testObj().get(1);
        Long id = 1L;
        doReturn(Optional.of(report))
                .when(reportRepository)
                .findById(id);
        doReturn(report)
                .when(reportRepository)
                .save(report);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/check/report/" + id)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void incorrectReportById() throws Exception {
        JSONObject jsonObject = (JSONObject) testObj().get(0);
        Report report = (Report) testObj().get(1);
        Long id = 1L;
        doReturn(Optional.of(report))
                .when(reportRepository)
                .findById(id);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/incorrect-report/" + id)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void increaseTheAdaptationPeriod14Day() throws Exception {
        JSONObject jsonObject = (JSONObject) testObj().get(0);
        Report report = (Report) testObj().get(1);
        User user = new User();
        doReturn(report)
                .when(reportRepository)
                .findByChatId(report.getChatId());
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(report.getChatId());
        Adoption adoption = new Adoption();
        doReturn(adoption)
                .when(adoptionRepository)
                .findByUser(user);
        doReturn(adoption)
                .when(adoptionRepository)
                .save(adoption);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/admin/increase-the-adaptation-period/14day/" + report.getChatId())
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void increaseTheAdaptationPeriod30Day() throws Exception {
        JSONObject jsonObject = (JSONObject) testObj().get(0);
        Report report = (Report) testObj().get(1);
        User user = new User();
        doReturn(report)
                .when(reportRepository)
                .findByChatId(report.getChatId());
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(report.getChatId());
        Adoption adoption = new Adoption();
        doReturn(adoption)
                .when(adoptionRepository)
                .findByUser(user);
        doReturn(adoption)
                .when(adoptionRepository)
                .save(adoption);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/admin/increase-the-adaptation-period/30day/" + report.getChatId())
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    private List testObj() throws JSONException {
        List list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        Long id = 1L;
        String text = "text";
        Long chatId = 1L;
        Long updatedAt = 1L;
        Boolean checked = false;
        Boolean looked = false;
        jsonObject.put("id",id);
        jsonObject.put("text",text);
        jsonObject.put("chatId",chatId);
        jsonObject.put("updatedAt",updatedAt);
        jsonObject.put("checked",checked);
        jsonObject.put("looked",looked);
        Report report = new Report();
        report.setChatId(id);
        report.setText(text);
        report.setChatId(chatId);
        report.setUpdatedAt(updatedAt);
        report.setChecked(checked);
        report.setLooked(looked);
        list.add(jsonObject);
        list.add(report);
        return list;
    }

}