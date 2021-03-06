package models

enum class Command {
    USER,
    PASS,
    ACCT,
    CWD,
    CDUP,
    SMNT,
    QUIT,
    REIN,
    PORT,
    PASV,
    TYPE,
    STRU,
    MODE,
    RETR,
    STOR,
    STOU,
    APPE,
    ALLO,
    REST,
    RNFR,
    RNTO,
    ABOR,
    DELE,
    RMD,
    MKD,
    PWD,
    LIST,
    NLST,
    SITE,
    SYST,
    STAT,
    HELP,
    NOOP
}