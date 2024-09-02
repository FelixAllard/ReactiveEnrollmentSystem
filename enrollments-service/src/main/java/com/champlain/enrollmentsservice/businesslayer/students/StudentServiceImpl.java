package com.champlain.enrollmentsservice.businesslayer.students;

import com.champlain.enrollmentsservice.businesslayer.students.StudentService;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientSynchronous;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientAsynchronous;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentResponseModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentClientSynchronous studentClientSynchronous;
    private final StudentClientAsynchronous studentClientAsynchronous;

    public StudentServiceImpl(StudentClientSynchronous studentClientSynchronous, StudentClientAsynchronous studentClientAsynchronous) {
        this.studentClientSynchronous = studentClientSynchronous;
        this.studentClientAsynchronous = studentClientAsynchronous;
    }

    @Override
    public Flux<StudentResponseModel> getAllStudents() {
        return studentClientAsynchronous.getAllStudents();
    }

    //synchronous
    @Override
    public List<StudentResponseModel> get1000StudentsSyncLoop() {
        return studentClientSynchronous.get1000StudentsLoop();
    }

    @Override
    public List<StudentResponseModel> get1000StudentsSyncParallel() {
        return studentClientSynchronous.get1000StudentsSyncParallel();
    }

    //asynchronous
    @Override
    public Flux<StudentResponseModel> get1000StudentsAsync() {
        return studentClientAsynchronous.get1000StudentsAsync();
    }

    @Override
    public Flux<StudentResponseModel> get1000StudentsAsyncParallel() {
        return studentClientAsynchronous.get1000StudentsAsyncParallel();
    }

    @Override
    public Flux<StudentResponseModel> get1000StudentsAsyncBounded() {
        return studentClientAsynchronous.get1000StudentsAsyncBounded();
    }

    @Override
    public Mono<StudentResponseModel> getStudentByStudentId(String studentId) {
        return studentClientAsynchronous.getStudentByStudentId(studentId);
    }
}
