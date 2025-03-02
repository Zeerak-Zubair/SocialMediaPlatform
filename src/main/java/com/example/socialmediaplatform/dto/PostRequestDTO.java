package com.example.socialmediaplatform.dto;

import com.example.socialmediaplatform.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {
    private String content;

    public Post toEntity(){
        return new Post(null,null,this.content,null,null,null);
    }
}
