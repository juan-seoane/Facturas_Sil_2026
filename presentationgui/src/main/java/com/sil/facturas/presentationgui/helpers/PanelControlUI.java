package com.sil.facturas.presentationgui.helpers;

import java.util.concurrent.BrokenBarrierException;

import com.sil.facturas.app.api.IPanelControlUI;
import com.sil.facturas.domain.enums._Boton;
import com.sil.facturas.domain.interfaces.IDebugService;
import com.sil.facturas.presentationgui.fxcontrollers.FxCntrlPanelControl;

public class PanelControlUI implements IPanelControlUI {

    @Override
    public void pulsar(_Boton boton) {
        FxCntrlPanelControl pc = FxCntrlPanelControl.getPanelControl();
        if (pc == null) return;

        switch (boton) {
			case FCT : pc.getBtnFCT().fire();
            case RS  : pc.getBtnRS().fire();
            case CJA : pc.getBtnCJA().fire();
			case CFG : pc.getBtnCFG().fire();
            case NTS : pc.getBtnNTS().fire();
        }
    }
}
