package DAT250.exercises.jpa.polls;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v WHERE v.votesOn.poll.id = :pollId")
    List<Vote> findByPollId(Long pollId);
}
