package br.com.quarkussocial.quarkussocial.rest;

import br.com.quarkussocial.quarkussocial.domain.model.User;
import br.com.quarkussocial.quarkussocial.domain.repository.UserRepository;
import br.com.quarkussocial.quarkussocial.rest.dto.CreateUserRequest;
import br.com.quarkussocial.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public class UserResource {

    private final UserRepository repository;
    private final Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator){
            this.repository = repository;
            this.validator = validator;
        }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){

        //tratamento de erros no conteúdo do request
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()){
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABEL_ENTITY_STATUS);
        }

        //seta dados request na entidade
        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        //persiste dados do user
        repository.persist(user);

        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(userRequest)
                .build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = repository.findById(id);
        if (user != null){
            repository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
        User user = repository.findById(id);
        if (user != null){
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
