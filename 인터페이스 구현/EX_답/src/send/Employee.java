package send;

/**
 * 사원 VO — ★ 정답
 *
 * <pre>
 * 학습모듈 1-2 §6 (데이터 표준)
 * 인터페이스설계서.md §3.3 / 부록 A
 * </pre>
 */
public class Employee {

    private String empId;        // VARCHAR(12) E + YYYYMMDD + 3자리
    private String empName;      // VARCHAR(50) UTF-8
    private String deptCd;       // CHAR(4)
    private String position;     // VARCHAR(20)
    private String hireDt;       // CHAR(8) YYYYMMDD
    private String email;        // VARCHAR(100)

    public Employee() {}

    public Employee(String empId, String empName, String deptCd,
                    String position, String hireDt, String email) {
        this.empId    = empId;
        this.empName  = empName;
        this.deptCd   = deptCd;
        this.position = position;
        this.hireDt   = hireDt;
        this.email    = email;
    }

    public String getEmpId()             { return empId; }
    public void   setEmpId(String v)     { this.empId = v; }
    public String getEmpName()           { return empName; }
    public void   setEmpName(String v)   { this.empName = v; }
    public String getDeptCd()            { return deptCd; }
    public void   setDeptCd(String v)    { this.deptCd = v; }
    public String getPosition()          { return position; }
    public void   setPosition(String v)  { this.position = v; }
    public String getHireDt()            { return hireDt; }
    public void   setHireDt(String v)    { this.hireDt = v; }
    public String getEmail()             { return email; }
    public void   setEmail(String v)     { this.email = v; }
}
