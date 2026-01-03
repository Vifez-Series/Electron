package lol.vifez.electron.game.elo;

import lombok.Data;

@Data
public class KFactor {
	private final int startIndex, endIndex;
	private final double value;
}