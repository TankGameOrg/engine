package pro.trevor.tankgame.rule.impl.util;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatus;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatusDecoder;

public class NullStatusDecoder implements IStatusDecoder {
    @Override
    public IStatus fromSource(JSONObject source) {
        throw new Error("Status effects not implemented for this version");
    }
}
