package com.sil.facturas.domain.interfaces;

import com.sil.facturas.domain.pojos.ConfigData;
import com.sil.facturas.domain.pojos.RutasConfig;

public interface IConfigService {
  ConfigData getConfigData(String usuario);

  RutasConfig getRutasConfig(String usuario);

  ConfigData loadConfig(String usuario);

  void saveConfig(String usuario, ConfigData data);
}
