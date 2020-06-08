package com.glamasw.petitamirestapi.repositories;

import com.glamasw.petitamirestapi.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    //Derived query:
    //Optional<Owner> findByDni(int dni);
    //The method names for derived queries can get quite long, and they are limited to just a single table.
    //When we need something more complex, we can write a custom query using @Query and @Modifying together.
    //Let's check the equivalent code for our derived method from earlier:
    @Query("from Owner o where o.dni=:dni")
    Optional<Owner> findByDni(@Param("dni") int dni);
    //Both solutions presented above are similar and achieve the same result. However, they take a slightly different approach.
    //The @Query method creates a single JPQL query against the database. By comparison, the deleteBy methods execute a read query,
    //then delete each of the items one by one.
}
