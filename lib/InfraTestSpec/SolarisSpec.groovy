package InfraTestSpec

import groovy.util.logging.Slf4j
import groovy.transform.InheritConstructors
import org.hidetake.groovy.ssh.Ssh
import jp.co.toshiba.ITInfra.acceptance.InfraTestSpec.*
import jp.co.toshiba.ITInfra.acceptance.*
import org.apache.commons.net.util.SubnetUtils
import org.apache.commons.net.util.SubnetUtils.SubnetInfo

@Slf4j
@InheritConstructors
class SolarisSpec extends LinuxSpecBase {

    def init() {
        super.init()
    }

    def finish() {
        super.finish()
    }

    def hostname(session, test_item) {
        def lines = exec('hostname') {
            run_ssh_command(session, 'uname -n', 'hostname')
        }
        lines = lines.replaceAll(/(\r|\n)/, "")
        test_item.results(lines)
    }

    def hostname_fqdn(session, test_item) {
        def lines = exec('hostname_fqdn') {
            run_ssh_command(session, 'awk \'/^domain/ {print $2}\' /etc/resolv.conf', 'hostname_fqdn')
        }
        lines = lines.replaceAll(/(\r|\n)/, "")
        test_item.results(lines)
    }

    def kernel(session, test_item) {
        def lines = exec('kernel') {
            run_ssh_command(session, ' uname -X', 'kernel')
        }
        def info = [:]
        lines.eachLine {
            (it =~ /^(System|Release|KernelID|Machine) = (.+?)$/).each {m0,m1,m2->
                info[m1] = m2
            }
        }
        test_item.results(info.toString())
    }

    def cpu(session, test_item) {
        def lines = exec('cpu') {
            run_ssh_command(session, 'kstat -p cpu_info', 'cpu')
        }

        def cpuinfo    = [:].withDefault{0}
        def real_cpu   = [:].withDefault{0}
        def cpu_number = 0
        lines.eachLine {
            (it =~ /ncpu_per_chip\s+(.+)$/).each {m0,m1->
                cpu_number += Integer.decode(m1)
            }
            (it =~ /chip_id\s+(.+)$/).each {m0,m1->
                real_cpu[m1] = true
            }
            (it =~ /ncore_per_chip\s+(.+)$/).each {m0,m1->
                cpuinfo["cores"] += Integer.decode(m1)
            }
            (it =~ /brand\s+(.+)$/).each {m0,m1->
                cpuinfo["model_name"] = m1
            }
            (it =~ /clock_MHz\s+(.+)/).each {m0,m1->
                cpuinfo["mhz"] = m1
            }
        }
        cpuinfo["cpu_total"] = cpu_number
        cpuinfo["cpu_real"] = real_cpu.size()
        test_item.results(cpuinfo)
    }

    def machineid(session, test_item) {
        def lines = exec('machineid') {
            run_ssh_command(session, 'hostid', 'machineid')
        }
        lines = lines.replaceAll(/(\r|\n)/, "")
        test_item.results(lines)
    }

    def memory(session, test_item) {
        def lines = exec('memory') {
            run_ssh_command(session, '/usr/sbin/prtconf |grep Memory', 'memory')
        }
        def memory_size = 0
        lines.eachLine {
            (it =~ /(\d+)/).each {m0,m1->
                memory_size += Integer.decode(m1)
            }
        }
        test_item.results(memory_size.toString())
    }

    def swap(session, test_item) {
        def lines = exec('swap') {
            run_ssh_command(session, '/usr/sbin/swap -s', 'swap')
        }
        Closure norm = { value, unit ->
            if (unit == 'k') {
                return value
            } else if (unit == 'm') {
                return value * 1024
            } else {
                return "${value}${unit}"
            }
        }
        def swap    = [:].withDefault{0}
        // swap -s
        // total: 10492k bytes allocated + 7840k reserved = 18332k used, 21568k available
        lines.eachLine {
            (it =~ /\s+(\d+)(.)\s+bytes allocated/).each {m0,m1,m2->
                swap['swap_alloc'] = norm(m1, m2)
            }
            (it =~ /\s+(\d+)(.)\s+reserved/).each {m0,m1,m2->
                swap['swap_reserve'] = norm(m1, m2)
            }
            (it =~ /\s+(\d+)(.)\s+available/).each {m0,m1,m2->
                swap['swap_total'] = norm(m1, m2)
            }
        }
        test_item.results(swap)
    }

    def network(session, test_item) {
        def lines = exec('network') {
            run_ssh_command(session, '/usr/sbin/ifconfig -a', 'network')
        }
        def csv = []
        def network = [:].withDefault{[:]}
        def device = ''
        def hw_address = []
        lines.eachLine {
            // e1000g0: flags=1000843<UP,BROADCAST,RUNNING,MULTICAST,IPv4> mtu 1500 index 2
            //         inet 192.168.10.3 netmask ffffff00 broadcast 192.168.10.255
            (it =~  /^(.+?): (.+)<(.+)> (.+)$/).each { m0,m1,m2,m3,m4->
                device = m1
                if (m1 == 'lo0') {
                    return
                }
                def index = 0
                def name  = ''
                m4.split(/ /).each{ n1->
                    if (index % 2 == 0) {
                        name = n1
                    } else {
                        network[device][name] = n1
                    }
                    index ++
                }
            }
            // inet 127.0.0.1/8 scope host lo
            (it =~ /inet\s+(.*?)\s/).each {m0, m1->
                network[device]['ip'] = m1
            }
            (it =~ /netmask\s+(.+?)[\s|]/).each {m0, m1->
                network[device]['subnet'] = m1
            }

            // ether 8:0:20:0:0:1
            (it =~ /ether\s+(.*?)\s*/).each {m0, m1->
                network[device]['mac'] = m1
                hw_address.add(m1)
            }
        }
        // mtu:1500, qdisc:noqueue, state:DOWN, ip:172.17.0.1/16
        network.each { device_id, items ->
            def columns = [device_id]
            ['ip', 'mtu', 'state', 'mac', 'subnet'].each {
                columns.add(items[it] ?: 'NaN')
            }
            csv << columns
        }
        def headers = ['device', 'ip', 'mtu', 'state', 'mac', 'subnet']
        test_item.devices(csv, headers)
        test_item.results([
            'network' : network.keySet().toString(),
            'hw_address' : hw_address.toString()
        ])
    }

    // def net_onboot(session, test_item) {
    //     def lines = exec('net_onboot') {
    //         def command = """\
    //         |cd /etc/sysconfig/network-scripts/
    //         |grep ONBOOT ifcfg-* >> ${work_dir}/net_onboot
    //         """.stripMargin()
    //         session.execute command
    //         session.get from: "${work_dir}/net_onboot", into: local_dir
    //         new File("${local_dir}/net_onboot").text
    //     }
    //     def net_onboot = [:]
    //     lines.eachLine {
    //         (it =~ /^ifcfg-(.+):ONBOOT=(.+)$/).each {m0,m1,m2->
    //             net_onboot[m1] = m2
    //         }
    //     }
    //     test_item.results(net_onboot.toString())
    // }

    def net_route(session, test_item) {
        def lines = exec('net_route') {
            run_ssh_command(session, '/usr/sbin/route -v -n get default', 'net_route')
        }
        def net_route   = [:]
        def mac_address = []

        // gateway: 192.168.10.254
        // interface: e1000g0 index 2 address 00 0c 29 6f 38 cf
        // flags: <UP,GATEWAY,DONE,STATIC>

        lines.eachLine {
            (it =~ /gateway: (.+)$/).each {m0,m1->
                net_route['net_route'] = m1
            }
            (it =~ /interface: .+ address (.+?)\s*$/).each {m0,m1->
                mac_address << m1
            }
        }
        net_route['mac'] = mac_address.toString()
        test_item.results(net_route)
    }


    def filesystem(session, test_item) {
        def lines = exec('filesystem') {
            run_ssh_command(session, 'df -ha', 'filesystem')
        }

        // Filesystem            Size  Used Avail Use% Mounted on
        // /dev/mapper/vg_ostrich-lv_root
        //                        26G   25G  184M 100% /
        // proc                     0     0     0    - /proc
        // sysfs                    0     0     0    - /sys
        // devpts                   0     0     0    - /dev/pts
        // tmpfs                 939M     0  939M   0% /dev/shm
        // /dev/sda1             477M   69M  383M  16% /boot
        // none                     0     0     0    - /proc/sys/fs/binfmt_misc
        def csv = []
        def filesystems = [:]
        lines.eachLine {
            (it =~  /\s+(\d.+)$/).each { m0,m1->
                def columns = m1.split(/\s+/)
                if (columns.size() == 5) {
                    def size  = columns[0]
                    def mount = columns[4]
                    (size =~ /^[1-9]/).each { row ->
                        filesystems['filesystem.' + mount] = size
                        csv << columns
                    }
                }
            }
        }
        def headers = ['size', 'used', 'avail', 'use%', 'mountpoint']
        test_item.devices(csv, headers)
        filesystems['filesystem'] = csv.size().toString()
        test_item.results(filesystems)
    }

    // def virturization(session, test_item) {
    //     def lines = exec('virturization') {
    //         run_ssh_command(session, 'cat /proc/cpuinfo', 'virturization')
    //     }
    //     def virturization = 'no KVM'
    //     lines.eachLine {
    //         if (it =~  /QEMU Virtual CPU|Common KVM processor|Common 32-bit KVM processor/) {
    //             virturization = 'KVM Guest'
    //         }
    //     }
    //     test_item.results(virturization)
    // }

    def packages(session, test_item) {
        def lines = exec('packages') {
            run_ssh_command(session, "/usr/bin/pkginfo -l", 'packages')
        }
        def pkginst
        def csv = []
        def row = []
        def package_infos = [:]
        lines.eachLine {
            (it =~ /(PKGINST|NAME|CATEGORY|ARCH|VERSION|VENDOR|INSTDATE):\s+(.+)$/).each {m0,m1,m2->
                row << m2
                if (m1 == 'PKGINST') {
                    pkginst = m2
                }
                if (m1 == 'VERSION') {
                    package_infos['packages.' + pkginst] = m2
                }
                if (m1 == 'INSTDATE') {
                    csv << row
                    row = []
                }
            }
        }
        def headers = ['pkginst', 'name', 'category', 'arch', 'version', 'vendor', 'instdate']
        test_item.devices(csv, headers)
        test_item.results(package_infos)
    }

    def user(session, test_item) {
        def lines = exec('user') {
            run_ssh_command(session, "cat /etc/passwd", 'user')
        }
        def group_lines = exec('group') {
            run_ssh_command(session, "cat /etc/group", 'group')
        }
        def groups = [:].withDefault{0}
        // root:x:0:
        group_lines.eachLine {
            ( it =~ /^(.+?):(.*?):(\d+)/).each {m0,m1,m2,m3->
                groups[m3] = m1
            }
        }
        def csv = []
        def user_count = 0
        def users = [:].withDefault{'unkown'}
        lines.eachLine {
            def arr = it.split(/:/)
            if (arr.size() > 4) {
                def username = arr[0]
                def user_id  = arr[2]
                def group_id = arr[3]
                def group    = groups[group_id] ?: 'Unkown'

                csv << [username, user_id, group_id, group]
                user_count ++
                users['user.' + username] = 'OK'
            }
        }
        def headers = ['UserName', 'UserID', 'GroupID', 'Group']
        test_item.devices(csv, headers)
        users['user'] = user_count
        test_item.results(users)
    }

    def service(session, test_item) {
        def lines = exec('service') {
            run_ssh_command(session, '/usr/bin/svcs -a | grep online', 'service')
        }
        def services = [:].withDefault{'unkown'}
        def csv = []
        def service_count = 0
        lines.eachLine {
            ( it =~ /svc:(.+?):/).each {m0,m1->
                def service_name = 'service.' + m1
                services[service_name] = 'On'
                def columns = [m1, 'On']
                csv << columns
                service_count ++
            }
        }
        services['service'] = service_count.toString()
        test_item.devices(csv, ['Name', 'Status'])
        test_item.results(services)
    }


    // def oracle(session, test_item) {
    //     def lines = exec('oracle') {
    //         def command = """\
    //         |ls -d /opt/oracle/app/product/*/*  >> ${work_dir}/oracle
    //         |ls -d /*/app/oracle/product/*/*    >> ${work_dir}/oracle
    //         """
    //         session.execute command.stripMargin()
    //         session.get from: "${work_dir}/oracle", into: local_dir
    //         new File("${local_dir}/oracle").text
    //     }
    //     def oracleinfo = 'NotFound'
    //     lines.eachLine {
    //         ( it =~ /\/product\/(.+?)\/(.+?)$/).each {m0,m1,m2->
    //             oracleinfo = m1
    //         }
    //     }
    //     test_item.results(oracleinfo)
    // }

    // def proxy_global(session, test_item) {
    //     def lines = exec('proxy_global') {
    //         run_ssh_command(session, 'grep proxy /etc/yum.conf', 'proxy_global')
    //     }
    //     lines = lines.replaceAll(/(\r|\n)/, "")
    //     test_item.results(lines)
    // }

    def resolve_conf(session, test_item) {
        def lines = exec('resolve_conf') {
            run_ssh_command(session, 'grep nameserver /etc/resolv.conf', 'resolve_conf')
        }
        def nameservers = [:]
        def nameserver_number = 1
        lines.eachLine {
            ( it =~ /^nameserver\s+(\w.+)$/).each {m0,m1->
                nameservers["nameserver${nameserver_number}"] = m1
                nameserver_number ++
            }
        }
        test_item.results([
            'resolve_conf' : (nameserver_number == 1) ? 'off' : 'on',
            'nameservers' : nameservers
        ])
    }

    def ntp(session, test_item) {
        def lines = exec('ntp') {
            run_ssh_command(session, "egrep -e '^server' /etc/inet/ntp.conf", 'ntp')
        }
        def ntpservers = []
        lines.eachLine {
            ( it =~ /^server\s+(\w.+)$/).each {m0,m1->
                ntpservers.add(m1)
            }
        }
        test_item.results(ntpservers.toString())
    }


}
