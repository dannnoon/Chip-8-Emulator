package pl.krysinski.chip8.domain.opcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpCode {
    final short raw;
    final byte stateA;
    final byte stateC;
    final byte stateD;
}
