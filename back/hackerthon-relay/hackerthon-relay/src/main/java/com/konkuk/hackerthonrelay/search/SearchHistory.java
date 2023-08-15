package com.konkuk.hackerthonrelay.search;

import com.konkuk.hackerthonrelay.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "search_history")
@Setter @Getter
public class SearchHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private User user;

	private String keyword;

}
