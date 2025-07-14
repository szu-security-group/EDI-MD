package com.fchen_group.EDISVMD.Core;

import java.io.IOError;
import java.io.IOException;

public abstract class AuditComponent {
    public abstract Key keyGen(int paramInt);

    public abstract void outSource();

    public abstract ChallengeData[] audit(int paramInt, Key paramKey);

    public abstract ProofData prove(int paramInt, byte[] paramBytes, ChallengeData paramChallengeData) throws IOException;

    public abstract boolean verify(ProofData[] paramArrayOfProofData);
}
