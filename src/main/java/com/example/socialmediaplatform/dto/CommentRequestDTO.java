package com.example.socialmediaplatform.dto;

import com.example.socialmediaplatform.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentRequestDTO {
    private String content;

    public Comment toEntity(){
        return new Comment(null,null,null,this.content,null);
    }
}
