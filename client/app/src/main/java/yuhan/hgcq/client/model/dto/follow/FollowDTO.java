package yuhan.hgcq.client.model.dto.follow;

import java.io.Serializable;

public class FollowDTO implements Serializable {
    private Long memberId;
    private Long followId;

    public FollowDTO() {
    }

    public FollowDTO(Long memberId, Long followId) {
        this.memberId = memberId;
        this.followId = followId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getFollowId() {
        return followId;
    }

    public void setFollowId(Long followId) {
        this.followId = followId;
    }

    @Override
    public String toString() {
        return "FollowDTO{" +
                "memberId=" + memberId +
                ", followId=" + followId +
                '}';
    }
}
