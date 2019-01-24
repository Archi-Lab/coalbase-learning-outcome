package de.archilab.coalbase.learningoutcome;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CoalbaseLearningOutcomeApplicationTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LearningOutcomeRepository learningOutcomeRepository;

  @Test
  public void createLearningOutcome() throws Exception {
    String competence = "{\"action\" : \"Die Studierenden können Marketingentscheidungen informationsgestützt treffen\", \"taxonomyLevel\" : \"Synthese (Stufe 5)\"}";
    String tool0 = "{\"value\" : \"das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren\"}";
    String tool1 = "{\"value\" : \"Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen\"}";
    String purpose = "{\"value\" : \"Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten\"}";
    String learningOutcomeJson =
        "{\"competence\" : " + competence + ", \"tools\" : [" + tool0 + ", " + tool1
            + "], \"purpose\" : " + purpose + "}";

    mvc.perform(post("/learningOutcomes").content(learningOutcomeJson)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201))
        .andExpect(jsonPath("$.competence", is(competence)))
        .andExpect(jsonPath("$.tools[0]", is(tool0)))
        .andExpect(jsonPath("$.tools[1]", is(tool1)))
        .andExpect(jsonPath("$.purpose", is(purpose)));
  }

}

