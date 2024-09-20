package yuhan.hgcq.client.model.dto.follow;

import java.io.Serializable;

public class FollowDTO implements Serializable {
    private Long followId;

    public FollowDTO() {
    }

    public FollowDTO(Long followId) {
        this.followId = followId;
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
                ", followId=" + followId +
                '}';
    }
}
