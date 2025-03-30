package com.example.nhatrobackend.Service;

public interface FollowerService {
    public String followUser(Integer followingUserId, Integer followedUserId);
    public String unfollowUser(Integer followingUserId, Integer followedUserId);
    boolean isFollowing(Integer followingUserId, Integer followedUserId);

}
