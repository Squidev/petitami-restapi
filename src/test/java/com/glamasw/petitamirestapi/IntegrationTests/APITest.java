package com.glamasw.petitamirestapi.IntegrationTests;

import com.glamasw.petitamirestapi.controllers.DogController;
import com.glamasw.petitamirestapi.entities.ContactMedium;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application.properties")

@SpringBootTest
@AutoConfigureMockMvc
@Execution(ExecutionMode.SAME_THREAD)
public class APITest {

    @Autowired
    DogController dogController;

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    @DisplayName("API test: Get all dogs, empty DB")
    public void getAllDogsTest(TestInfo testInfo) throws Exception {
        MvcResult result =   mvc.perform(get("/api/v1/dog").content("").contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[{}]"))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
        }

    public void getOneDogTest(TestInfo testInfo) {

    }

    @Order(2)
    @DisplayName("Api test: Save dogs")
    @RepeatedTest(value = 5, name = "{displayName} ---> Dog {currentRepetition}")
    public void saveDogTest(RepetitionInfo repetitionInfo) throws Exception {
        String dogName = "Chocoperro "+repetitionInfo.getCurrentRepetition();
        String ownerName = "Chocoperro Owner "+repetitionInfo.getCurrentRepetition();
        int ownerDNI = 47518490+repetitionInfo.getCurrentRepetition();

        //Contact Mediums creation
        List<ContactMedium> contactMediums = new ArrayList();

        ContactMedium contactMedium1 = new ContactMedium();
        contactMedium1.setType("\"Facebook\"");
        contactMedium1.setValue("\"www.facebook.com/" + ownerName.replace(" ","") + "\"");
        contactMediums.add(contactMedium1);

        ContactMedium contactMedium2 = new ContactMedium();
        contactMedium2.setType("\"Instagram\"");
        contactMedium2.setValue("\"www.instagram.com/"+ownerName.replace(" ","") + "\"");
        contactMediums.add(contactMedium2);

        ContactMedium contactMedium3 = new ContactMedium();
        contactMedium3.setType("\"WhatsApp\"");
        contactMedium3.setValue("\"261487154"+repetitionInfo.getCurrentRepetition() + "\"");
        contactMediums.add(contactMedium3);

        //JSON formatting
        String dogJSON = "{dogName: \""+dogName+"\", ownerName: \""+ownerName+"\", contactMediums: "+contactMediumsToString(contactMediums)+"}";

        //HTTP request sending
        MvcResult result =   mvc.perform(post("/api/v1/dog/").content(dogJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dogJSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        //Asserts
        System.out.println("dogJSON: "+dogJSON);
        System.out.println("result.toString(): "+result.toString());
        assertEquals(dogJSON, result.toString());
    }

    String contactMediumsToString(List<ContactMedium> contactMediums) {
        //Se abre el array
        String stringifiedContactMediums = "[";
        //Se convierte cada ContactMedium a su formato JSON y se añade al String.
        for (ContactMedium cm: contactMediums) {
            stringifiedContactMediums = stringifiedContactMediums + "{contactMediumName: "+cm.getType()+", contactMediumValue: "+cm.getValue()+"}";
            //Si no se llegó al final del array, se agrega una "," para separar los JSON.
            if (contactMediums.indexOf(cm)!=contactMediums.toArray().length-1){
                stringifiedContactMediums += ", ";
            }
        }
        //Se cierra el array
        stringifiedContactMediums += "]";
        return stringifiedContactMediums;
    }

    public void updateDogTest(TestInfo testInfo) {

    }

    public void deleteDogTest(TestInfo testInfo) {

    }

    /*public void deleteDogTest(TestInfo testInfo) {
        String[] dnaArray = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        MvcResult result = mvc.perform(post("/mutant/").content("{\"dna\":"+asJsonString(dnaArray)+"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("OK"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        System.out.println("JSON send: {\"dna\":"+asJsonString(dnaArray)+"} Api Return ===> "+content);
    }*/
}