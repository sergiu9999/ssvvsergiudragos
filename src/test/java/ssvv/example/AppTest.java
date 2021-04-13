package ssvv.example;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import ssvv.example.domain.Nota;
import ssvv.example.domain.Student;
import ssvv.example.domain.Tema;
import ssvv.example.repository.NotaXMLRepo;
import ssvv.example.repository.StudentXMLRepo;
import ssvv.example.repository.TemaXMLRepo;
import ssvv.example.service.Service;
import ssvv.example.validation.NotaValidator;
import ssvv.example.validation.StudentValidator;
import ssvv.example.validation.TemaValidator;
import ssvv.example.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Unit test for simple App.
 */
public class AppTest
{

    private Service service;

    @Before
    public void initializeRepositoriesAndService() {
        StudentValidator studentValidator = new StudentValidator();
        TemaValidator temaValidator = new TemaValidator();
        String filenameStudent = "fisiere/Studenti.xml";
        String filenameTema = "fisiere/Teme.xml";
        String filenameNota = "fisiere/Note.xml";
        StudentXMLRepo studentXMLRepository = new StudentXMLRepo(filenameStudent);
        TemaXMLRepo temaXMLRepository = new TemaXMLRepo(filenameTema);
        NotaValidator notaValidator = new NotaValidator(studentXMLRepository, temaXMLRepository);
        NotaXMLRepo notaXMLRepository = new NotaXMLRepo(filenameNota);
        this.service = new Service(studentXMLRepository, studentValidator, temaXMLRepository, temaValidator, notaXMLRepository, notaValidator);
    }

    @Test
    public void addStudentTest_studentAddedSuccessfully() {
        var initialSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        service.addStudent(new Student(UUID.randomUUID().toString(), "Drg Sab", 934, "dragos.sabau@hibyte.ro"));

        var afterAddSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        assertEquals(initialSize + 1, afterAddSize);
    }

    @Test
    public  void addStudentDuplicateID_wontAddNewEntry() {
        var studentId = UUID.randomUUID().toString();

        var initialSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        service.addStudent(new Student(studentId, "Drg Sab", 934, "dragos.sabau@hibyte.ro"));

        var afterAddSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        assertEquals(initialSize + 1, afterAddSize);

        service.addStudent(new Student(studentId, "Drg Sab", 934, "dragos.sabau@hibyte.ro"));

        assertEquals(initialSize + 1, afterAddSize);
    }

    @Test
    public void addAssignment_assignmentAddedSuccessfully() {
        var initialSize = service.getAllTeme().spliterator().getExactSizeIfKnown();

        service.addTema(new Tema(UUID.randomUUID().toString(), "Lab 3 Assignment", 3, 4));

        var afterAddSize = service.getAllTeme().spliterator().getExactSizeIfKnown();

        assertEquals(initialSize + 1, afterAddSize);
    }

    @Test
    public void addAssignment_emptyDescription_wontAddNewEntry() {
        Assertions.assertThrows(ValidationException.class, () -> service.addTema(new Tema(UUID.randomUUID().toString(), "", 3, 4)));
    }

    @Test
    public void addAssignment_emptyId_wontAddNewEntry() {
        Assertions.assertThrows(ValidationException.class, () -> service.addTema(new Tema("", "descr", 3, 4)));
    }

    @Test
    public void addAssignment_deadlineNotInLimits_wontAddNewEntry() {
        Assertions.assertThrows(ValidationException.class, () -> service.addTema(new Tema(UUID.randomUUID().toString(), "descr", 3, 25)));
    }

    @Test
    public void addStudent_addAssignmentForStudent() {
        var studentId = UUID.randomUUID().toString();

        var initialSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        service.addStudent(new Student(studentId, "Drg Sab", 934, "dragos.sabau@hibyte.ro"));

        var afterAddSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        assertEquals(initialSize + 1, afterAddSize);

         var initialSizeAssignment = service.getAllTeme().spliterator().getExactSizeIfKnown();

        service.addTema(new Tema(UUID.randomUUID().toString(), "Lab 3 Assignment", 3, 4));

        var afterAddSizeAssignment = service.getAllTeme().spliterator().getExactSizeIfKnown();

        assertEquals(initialSizeAssignment + 1, afterAddSizeAssignment);
    }

    @Test
    public void addStudent_addAssignment_addGrade_test() {
        var studentId = UUID.randomUUID().toString();

        var initialSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        service.addStudent(new Student(studentId, "Drg Sab", 934, "dragos.sabau@hibyte.ro"));

        var afterAddSize = service.getAllStudenti().spliterator().getExactSizeIfKnown();

        assertEquals(initialSize + 1, afterAddSize);

         var initialSizeAssignment = service.getAllTeme().spliterator().getExactSizeIfKnown();

         var temaId = UUID.randomUUID().toString();

        service.addTema(new Tema(temaId, "Lab 3 Assignment", 11, 12));

        var afterAddSizeAssignment = service.getAllTeme().spliterator().getExactSizeIfKnown();

        assertEquals(initialSizeAssignment + 1, afterAddSizeAssignment);

        service.addNota(new Nota(UUID.randomUUID().toString(), studentId, temaId, 9, LocalDate.parse("15/12/2018", DateTimeFormatter.ofPattern("dd/MM/yyyy"))), "Very good job");
    }
}
