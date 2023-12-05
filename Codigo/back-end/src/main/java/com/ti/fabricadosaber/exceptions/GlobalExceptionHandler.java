package com.ti.fabricadosaber.exceptions;
//Classe que captura as exceções



import com.ti.fabricadosaber.services.exceptions.AuthorizationException;
import com.ti.fabricadosaber.services.exceptions.DataBindingViolationException;
import com.ti.fabricadosaber.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.io.IOException;


@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler {

    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    //@Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validacao. Verifique o log de erros para mais detalhes.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request) {
        final String errorMessage = "Erro desconhecido ocorrido";
        log.error(errorMessage, exception);
        return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    // Quando a exeção de integridade dos dados for lançada, tal exceção será capturada
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request) {
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Falha para salvar a entidade com os seguintes problemas: " + errorMessage,
                dataIntegrityViolationException);
        return buildErrorResponse(
                dataIntegrityViolationException,
                errorMessage,
                HttpStatus.CONFLICT,
                request);
    }


    // Erro de violação a regra pré-estabelecida
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Falha ao validar o elemento", constraintViolationException);
        return buildErrorResponse(
                constraintViolationException,
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    //todo: Exceção feita no pacote exception da camada service
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request) {
        log.error("Falha ao encontrar o objeto solicitado", objectNotFoundException);
        return buildErrorResponse(
                objectNotFoundException,
                HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request) {
        log.error("Falha para salvar entidade com dados associados", dataBindingViolationException);
        return buildErrorResponse(
                dataBindingViolationException,
                HttpStatus.CONFLICT,
                request);
    }


    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException authenticationException,
            WebRequest request) {
        log.error("Authentication error ", authenticationException);
        return buildErrorResponse(
                authenticationException,
                HttpStatus.UNAUTHORIZED,
                request);
    }





    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException accessDeniedException,
            WebRequest request) {
        log.error("Authorization error ", accessDeniedException);
        return buildErrorResponse(
                accessDeniedException,
                HttpStatus.FORBIDDEN,
                request);
    }


    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAuthorizationException(
            AuthorizationException authorizationException,
            WebRequest request) {
        log.error("Authorization error ", authorizationException);
        return buildErrorResponse(
                authorizationException,
                HttpStatus.FORBIDDEN,
                request);
    }




    /*------------OUTRAS EXCEÇÕES - ÍNICIO ------------*/


    @ExceptionHandler(TwoParentsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleTwoParentsException(TwoParentsException twoParentsException,
                                                           WebRequest request) {

        log.error("quantidade inadequada de responsáveis", twoParentsException);
        return buildErrorResponse(twoParentsException, HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException,
                                                                WebRequest request) {

        log.error("Entidade nao encontrada", entityNotFoundException);
        return buildErrorResponse(entityNotFoundException, HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(StudentOnTeamException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleStudentOnTeamException(StudentOnTeamException studentOnTeamException,
                                                               WebRequest request) {

        log.error("Falha ao remover estudante da turma", studentOnTeamException);
        return buildErrorResponse(studentOnTeamException, HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(DataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleDataException(DataException dataException,
                                                               WebRequest request) {

        log.error("Data incorreta", dataException);
        return buildErrorResponse(dataException, HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler(StudentTeamAssociationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleStudentTeamAssociationException(StudentTeamAssociationException studentTeamAssociationException,
                                                      WebRequest request) {

        log.error("Falha em relacionar aluno em turma", studentTeamAssociationException);
        return buildErrorResponse(studentTeamAssociationException, HttpStatus.BAD_REQUEST, request);
    }




    /*------------OUTRAS EXCEÇÕES - FIM ------------*/

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        int status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(status, "Email ou senha invalidos");
        response.getWriter().append(errorResponse.toJson());
    }


}
