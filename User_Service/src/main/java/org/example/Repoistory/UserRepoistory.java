package org.example.Repoistory;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepoistory extends JpaRepository<User, Long> {
}
