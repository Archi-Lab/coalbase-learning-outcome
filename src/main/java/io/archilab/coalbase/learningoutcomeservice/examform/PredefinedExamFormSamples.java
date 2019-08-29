package io.archilab.coalbase.learningoutcomeservice.examform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PredefinedExamFormSamples {

  @Autowired
  private PredefinedExamFormRepository predefinedExamFormRepository;

  @PostConstruct
  private void persistSamples() {
    String delivery = "Abgabe";
    String minutes = "Min";

    // Schedule Block 1
    List<Schedule> schedules1 = new ArrayList<>();
    schedules1.add(new Schedule("Während des Teilmodulblocks"));
    schedules1.add(new Schedule("Ende des Teilmodulblocks"));
    schedules1.add(new Schedule("Prüfungswoche"));

    // Schedule Block 2
    List<Schedule> schedules2 = new ArrayList<>();
    schedules2.add(new Schedule("Deadline (max. Semesterende)"));

    // Schedule Block 3
    List<Schedule> schedules3 = new ArrayList<>();
    schedules3.add(new Schedule("Ende des Teilmodulblocks"));
    schedules3.add(new Schedule("Prüfungswoche"));

    // Schedule Block 4
    List<Schedule> schedules4 = new ArrayList<>();
    schedules4.add(new Schedule("Semesterende"));

    // Schedule Block 5
    List<Schedule> schedules5 = new ArrayList<>();
    schedules5.add(new Schedule("Ende des Teilmodulblocks"));
    schedules5.add(new Schedule("Deadline (max. Semesterende)"));

    // Coding Session
    PredefinedExamForm predefinedExamForm = this
        .createPredefinedExamForm("Coding Session", schedules1,
        30, 180, minutes,
        "In den Live Coding Sessions werden die Prüflinge vor die Aufgabe gestellt ein Softwareartefakt (z.B. einen Algorithmus) in begrenzter Zeit zu entwickeln. Grundlage sind hierbei vom Lehrenden gegebene funktionale Anforderungen oder User Stories. Die Bewertung gründet sich auf im Vorfeld klar kommunizierten, transparenten Kriterien wie etwa Erfüllungsgrad der Anforderungen (Bestehen von Unit-Tests) oder Qualität des Codes (Statische Codeanalyse).");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Diskussion
    predefinedExamForm = this.createPredefinedExamForm("Diskussion", schedules1, 30, 60, minutes,
        "Die Prüflinge diskutieren in Gruppen untereinander und/oder zusammen mit Gästen aus der Wirtschaft ein durch den Lehrenden gestelltes Thema des jeweiligen Fachgebiet. Die Prüflinge stellen so ihre Kenntnisse der Fachsprache und Konzepte des jeweiligen Teilmoduls unter Beweis.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Dokumentation
    predefinedExamForm = this.createPredefinedExamForm("Dokumentation", schedules2, 0, 0, delivery,
        "Die Prüflinge erstellen eine Dokumentation für ein im praktischen Teil des Teilmoduls entwickeltes Produkt mit gängigen agilen Dokumentationsmethoden. Die Dokumentation thematisiert hier entweder das Produkt selbst und/oder dokumentiert den Entwicklungsprozess.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Essay
    predefinedExamForm = this.createPredefinedExamForm("Essay", schedules2, 0, 0, delivery,
        "Die Studierenden erstellen eine Abhandlung über ein wissenschaftliches, kulturelles oder gesellschaftliches Phänomene und stellen ihre subjektive Meinung über das Thema dar. Bewertet wird hierbei nicht die dargelegte Meinung des Studierendnen, sondern logische Begründung und Herleitung dieser.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Fachgespräch
    predefinedExamForm = this.createPredefinedExamForm("Fachgespräch", schedules3, 10, 15, minutes,
        "Der Prüfling stellt in einem kurzen, fachlichem Vieraugengespräch mit dem Lehrenden seinen Umgang mit der Fachsprache und grundlegenden Konzepten des jeweiligen Teilmoduls unter Beweis. Es thematisiert dabei zumeist ein im Teilmodul durchgeführtes Projekt und reflektiert die Beteiligung des Studierenden am Arbeits- und Entwicklungsprozesses.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Klausur
    predefinedExamForm = this.createPredefinedExamForm("Klausur", schedules3, 10, 0,
        "Min pro Creditpoint",
        "In den schriftlichen Klausurarbeiten soll der Prüfling nachweisen, dass er in begrenzter Zeit und mit beschränkten Hilfsmitteln Probleme aus Gebieten des jeweiligen Teilmoduls mit geläufigen wissenschaftlichen Methoden seiner Fachrichtung erkennt und auf richtigem Wege zu einer Lösung finden kann.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Mündliche Prüfung
    predefinedExamForm = this
        .createPredefinedExamForm("Mündliche Prüfung", schedules3, 30, 0, minutes,
        "In einer mündlichen Prüfung stellt der Prüfling sein Wissen über Methoden und Konzepte des jeweiligen Teilmoduls durch Beantwortung von durch den Lerhenden gestellten Fragen unter Beweis.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Multiple Choice/Lückentext
    predefinedExamForm = this
        .createPredefinedExamForm("Multiple Choice/Lückentext", schedules1, 10, 30,
        minutes,
        "Im Multiple Choice bzw. Lückentext soll der Prüfling sein Wissen über Konzepte des jeweiligen Fachgebiets unter Beweis stellen - anders als bei der schriftlichen Prüfung wird hierbei auf Freitextantworten verzichtet. Der Test kann hierbei auch zur Bewertung von Zwischenständen eines Teilmoduls genutzt werden, beispielsweise am Ende der ersten Woche des Teilmoduls.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Pitch
    predefinedExamForm = this.createPredefinedExamForm("Pitch", schedules1, 5, 10, minutes,
        "Die Studierenden präsentieren eine im Vorfeld entwickelte Produkt- oder Geschäftsideen und geben dem Lehrenden, Kommilitionen und Gästen in kurzer Zeit einen zusammenfassenden, informativen Überblick.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Portfolio
    predefinedExamForm = this.createPredefinedExamForm("Portfolio", schedules4, 0, 0, delivery,
        "Die Studierenden dokumentieren semesterbegleitend ihre gemeinnützigen Tätigkeiten im Modul »Community & Reflection« und evaluieren hierbei kritisch ihre eigene Leistung und Lernerfolg.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Poster Session
    predefinedExamForm = this.createPredefinedExamForm("Poster Session", schedules1, 5, 10, minutes,
        "Die Studierenden erstellen im Vorfeld der Session ein (wissenschaftliches) Poster zu einem erarbeiteten Produkt oder Thema und präsentieren ihre Ergebnisse sowohl dem Lehrendnen als auch sonstigen Gäasten und Kommilitionen. Die eigentliche Prüfungssituation mit dem Lehrenden dauert hierbei im Regelfall 5-10 Min, die Session an sich kann dabei aber durchaus länger dauern. Der Lehrende kann dabei neben seiner eigenen Bewertung des Ergebnisses auch das Feedback von anderen Gästen der Poster Session einbeziehen.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Präsentation
    predefinedExamForm = this.createPredefinedExamForm("Präsentation", schedules1, 10, 20, minutes,
        "Der Prüfling präsentiert dem Lehrenden und ggf. anderen Zuhörern ein von ihm erarbeitetes Thema oder \"fertiges\" Produkt und hinterfragt dabei kritisch sowohl das Ergebnis als auch den Arbeits- und Entwicklungsprozess.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Präsentation Work in Progress
    predefinedExamForm = this
        .createPredefinedExamForm("Präsentation Work in Progress", schedules1, 10,
        20, minutes,
        "Die Studierenden präsentieren hier den derzeitigen Entwicklungständ eines größeren Produkts in Form eines Minimum Viable Product und stellen so unter Beweis, dass ihr Entwicklungsinkrement sich ständig in einem funktionsfähigen Zustand befindet. Ziel hierbei ist es vor Allem frühzeitiges Feedback durch die Nutzer einzuholen und auf sich änderende Anforderungen agil reagieren zu können. Bewertet wird hierbei sowohl der aktuelle Zwischenstand als auch die Studenden erhobenen Feedbackergebnisse und geplante Änderungen/nächste Schritte.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Produktabgabe
    predefinedExamForm = this.createPredefinedExamForm("Produktabgabe", schedules5, 0, 0, delivery,
        "Die Studierenden entwickeln allein oder im Team Soft- und Hardwareprodukte, die durch den Lehrenden anhand von gängigen (Software-)Qualitätskriterien bewertet werden.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Projektbericht
    predefinedExamForm = this.createPredefinedExamForm("Projektbericht", schedules2, 0, 0, delivery,
        "Im Projektbericht stellen die Studierenden den Entwicklungsprozess eines Projekts dar und hinterfragen diesen kritisch.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);

    // Video
    predefinedExamForm = this.createPredefinedExamForm("Video", schedules2, 0, 0, delivery,
        "Die Studierenden erstellen ein zusammenfassendes Image- oder Werbevideo zu einem im Vorfeld erarbeiteten Produkt.");
    this.addPredefinedExamFormIfNotExist(predefinedExamForm);
  }

  private void addPredefinedExamFormIfNotExist(PredefinedExamForm predefinedExamForm) {
    Optional<PredefinedExamForm> optionalPredefinedExamForm = this.predefinedExamFormRepository
        .findByType(predefinedExamForm.getType());
    if (!optionalPredefinedExamForm.isPresent()) {
      this.predefinedExamFormRepository.save(predefinedExamForm);
    }
  }

  private PredefinedExamForm createPredefinedExamForm(String type, List<Schedule> schedules,
      int minValue, int maxValue, String unit, String description) {
    ExamType typeWrapper = new ExamType(type);

    Scope scopeWrapper = new Scope(minValue, maxValue, unit);

    ExamDescription descriptionWrapper = new ExamDescription(description);

    return new PredefinedExamForm(typeWrapper, schedules, scopeWrapper, descriptionWrapper);
  }
}
