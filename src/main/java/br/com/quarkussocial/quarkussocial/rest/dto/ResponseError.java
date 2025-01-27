package br.com.quarkussocial.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ResponseError {

    public static final int UNPROCESSABEL_ENTITY_STATUS = 422;
    private String message;
    private Collection<FieldError> errors;


    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations){

        List<FieldError> errors = violations
                .stream()
                .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());

//        String message = "Validation Error";
//        var responseError = new ResponseError("Validation Error", errors); OU
        return new ResponseError("Validation Error", errors);
    }

    public Response withStatusCode(int code){
        return Response.status(code).entity(this).build();
    }
}
