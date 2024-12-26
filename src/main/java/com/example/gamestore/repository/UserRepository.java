package com.example.gamestore.repository;

import com.example.gamestore.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    Optional<User> findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.dateOfBirthday = :dateOfBirthday WHERE u.id = :id")
    void updateDateOfBirthday(@Param("dateOfBirthday") LocalDate dateOfBirthday, @Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :firstName WHERE u.id = :id")
    void updateFirstName(@Param("firstName") String firstName, @Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastName = :lastName WHERE u.id = :id")
    void updateLastName(@Param("lastName") String lastName, @Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.picUri = :picUri WHERE u.id = :id")
    void updatePicUri(@Param("picUri") String picUri, @Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.balance = :balance WHERE u.id = :id")
    void updateBalance(@Param("balance") int balance, @Param("id") UUID id);
}
