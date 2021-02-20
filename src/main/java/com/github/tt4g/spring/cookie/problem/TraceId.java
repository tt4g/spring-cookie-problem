package com.github.tt4g.spring.cookie.problem;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class TraceId implements Serializable {

    @Serial
    private static final long serialVersionUID = 3248285715789509918L;

    private final String traceId;

    public TraceId(String traceId) {
        if (traceId.isEmpty()) {
            throw new IllegalArgumentException("traceId is empty.");
        }

        this.traceId = traceId;
    }

    public String getTraceId() {
        return this.traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TraceId traceId1 = (TraceId) o;
        return this.traceId.equals(traceId1.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.traceId);
    }

    @Override
    public String toString() {
        return "TraceId(" + this.traceId + ")";
    }
}

