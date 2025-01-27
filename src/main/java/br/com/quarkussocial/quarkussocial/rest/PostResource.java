package br.com.quarkussocial.quarkussocial.rest;

import br.com.quarkussocial.quarkussocial.domain.model.Post;
import br.com.quarkussocial.quarkussocial.domain.model.User;
import br.com.quarkussocial.quarkussocial.domain.repository.PostRepository;
import br.com.quarkussocial.quarkussocial.domain.repository.UserRepository;
import br.com.quarkussocial.quarkussocial.rest.dto.CreatePostRequest;
import br.com.quarkussocial.quarkussocial.rest.dto.PostResponse;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/user/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @POST
    @Transactional
    public Response createPost(@PathParam("userId") Long userId, CreatePostRequest postRequest){
        User user = userRepository.findById(userId);
        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(postRequest.getText());
        post.setUser(user);

        postRepository.persist(post);

        var postResponse = PostResponse.fromEntity(post);

        return Response
                .status(Response.Status.CREATED)
                .entity(postResponse)
                .build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);
        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // inferença de tipo - Java 11 (var teste = ...) | Podemos passar o SORT como 2º parâmetro do find
        var query = postRepository.find(
                "user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var postList = query.list();


        // expressão lambda pode ser substituida por "method reference"
        var postResponseList = postList.stream()
        //        .map(post -> PostResponse.fromEntity(post))
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
}
