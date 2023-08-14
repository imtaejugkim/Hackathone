package com.konkuk.hackerthonrelay.pictureupload.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.konkuk.hackerthonrelay.pictureupload.Post;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter @Getter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;


    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    public Tag(){
    }
    public Tag(String tagName) {
        this.name = tagName;
    }

    public TagDto tagDto(Long id, String name) {
        TagDto dto = new TagDto(this.getId(), this.getName());



        return dto;
    }

}
