package de.archilab.coalbase.learningoutcomeservice.core.local;

import org.hibernate.dialect.H2Dialect;

import java.sql.Types;


public class H2DialectCustom extends H2Dialect {

  public H2DialectCustom() {
    super();
    this.registerColumnType(Types.BINARY, "varbinary");
  }
}
