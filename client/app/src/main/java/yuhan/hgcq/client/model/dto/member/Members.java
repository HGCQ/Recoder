package yuhan.hgcq.client.model.dto.member;

import java.io.Serializable;
import java.util.List;

public class Members implements Serializable {
    private List<MemberDTO> memberList;
    private List<MemberDTO> followingList;

    public Members() {
    }

    public Members(List<MemberDTO> memberList, List<MemberDTO> followingList) {
        this.memberList = memberList;
        this.followingList = followingList;
    }

    public List<MemberDTO> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<MemberDTO> memberList) {
        this.memberList = memberList;
    }

    public List<MemberDTO> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<MemberDTO> followingList) {
        this.followingList = followingList;
    }
}
