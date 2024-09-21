package yuhan.hgcq.client.model.dto.follow;

import java.io.Serializable;
import java.util.List;

import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class Follower implements Serializable {
    private List<MemberDTO> followerList;
    private List<MemberDTO> followingList;

    public List<MemberDTO> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<MemberDTO> followerList) {
        this.followerList = followerList;
    }

    public List<MemberDTO> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<MemberDTO> followingList) {
        this.followingList = followingList;
    }
}
