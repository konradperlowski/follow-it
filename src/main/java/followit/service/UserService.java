package followit.service;

import com.google.common.collect.ImmutableList;
import followit.dao.UserRepository;
import followit.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUser(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean isUserInDb(String login) {
        return userRepository.existsByLogin(login);
    }

    public List<User> getAll() {
        return ImmutableList.copyOf(userRepository.findAll());
    }

    public List<User> getMostFollowed() {
        return userRepository.findMostFollowed();
    }

    public int getFollowingNumber(String userLogin) {
        return userRepository.findByFollowingLogin(userLogin).size();
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public List<User> getRecommendedToFollow(User user) {
        List<User> toReturn = user.getFollowing().size() > 3 ? userRepository.findRecommendedToFollow(user.getLogin()) :
                getMostFollowed().stream().filter(u -> !u.getLogin().equals(user.getLogin())).collect(Collectors.toList());
        return toReturn.stream().limit(5).collect(Collectors.toList());
    }

    public void follow(User user, User toFollow) {
        user = userRepository.findByLogin(user.getLogin());
        user.follows(userRepository.findByLogin(toFollow.getLogin()));
        userRepository.save(user);
    }

    @Transactional
    public void unfollow(User user, User toUnfollow) {
        user = userRepository.findByLogin(user.getLogin());
        user.unfollow(userRepository.findByLogin(toUnfollow.getLogin()));
        userRepository.save(user);
    }
}
