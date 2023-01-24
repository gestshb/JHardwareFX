package com.spring;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.hardware.CentralProcessor.TickType;
import oshi.software.os.*;
import oshi.software.os.OperatingSystem.ProcessSort;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {
    @FXML
    TextArea textInfo;

    @FXML
    void print() {

        String jasper = "/report.jasper";
        Map<String, Object> params = new HashMap<>();
        params.put("TEXT", textInfo.getText());


        new JasperViewerFX()
                .init(jasper, params, new JREmptyDataSource())
                .show();
    }

    @FXML
    void save() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        try {
            FileUtils.writeStringToFile(file, textInfo.getText(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void getInfo() {

        Logger LOG = LoggerFactory.getLogger(Controller.class);

        LOG.info("Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        System.out.println(os);

        LOG.info("Checking computer system...");
        printComputerSystem(hal.getComputerSystem());

        LOG.info("Checking Processor...");
        printProcessor(hal.getProcessor());








        LOG.info("Checking Disks...");
        printDisks(hal.getDiskStores());



        /*LOG.info("Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());*/

      /*  LOG.info("Checking Network parameterss...");
        printNetworkParameters(os.getNetworkParams());*/

      /*  // hardware: displays
        LOG.info("Checking Displays...");
        printDisplays(hal.getDisplays());*/

        /*// hardware: USB devices
        LOG.info("Checking USB Devices...");
        printUsbDevices(hal.getUsbDevices(true));
*/


    }

    @FXML
    void exit(ActionEvent event) {

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    void format(String s, Object... o) {
        String text = textInfo.getText();
        text += "\n" + String.format(s, o) + "\n";
        textInfo.setText(text);
    }

    void println(String s) {
        String text = textInfo.getText();
        text += "\n" + s + "\n";
        textInfo.setText(text);
    }


    private void printComputerSystem(final ComputerSystem computerSystem) {
/*
        println("manufacturer: " + computerSystem.getManufacturer());
        println("model: " + computerSystem.getModel());
        println("serialnumber: " + computerSystem.getSerialNumber());*/
       /* final Firmware firmware = computerSystem.getFirmware();
        println("firmware:");
        println("  manufacturer: " + firmware.getManufacturer());
        println("  name: " + firmware.getName());
        println("  description: " + firmware.getDescription());
        println("  version: " + firmware.getVersion());
        println("  release date: " + (firmware.getReleaseDate() == null ? "unknown"
                : firmware.getReleaseDate() == null ? "unknown" : firmware.getReleaseDate()));*/
        final Baseboard baseboard = computerSystem.getBaseboard();

        println("Motherboard:");
        println("     manufacturer: " + baseboard.getManufacturer());
        println("     model: " + baseboard.getModel());
        println("     version: " + baseboard.getVersion());
        println("     serialnumber: " + baseboard.getSerialNumber());
    }

    private void printProcessor(CentralProcessor processor) {
        println("**********************************************************");
        println("Processor:");
        println(processor.toString());
       /* println("  ------ " + processor.getPhysicalPackageCount() + " physical CPU package(s)");
        println("  ------ " + processor.getPhysicalProcessorCount() + " physical CPU core(s)");
        println("  ------ " + processor.getLogicalProcessorCount() + " logical CPU(s)");
        println("Processor ID : " + processor.getProcessorIdentifier().getProcessorID());
        println("Processor Identifier : " + processor.getProcessorIdentifier().getIdentifier());*/


    }

    /*private void printMemory(GlobalMemory memory) {
        println("Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + "/"
                + FormatUtil.formatBytes(memory.getTotal()));
        println("Swap used: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/"
                + FormatUtil.formatBytes(memory.getSwapTotal()));
    }*/

   /* private void printCpu(CentralProcessor processor) {

        *//*println("Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        println("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        println("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu);
        format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
        format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
        double[] loadAverage = processor.getSystemLoadAverage(3);
        println("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks();
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        println(procCpu.toString());*//*
    }
*/
   /* private void printProcesses(OperatingSystem os, GlobalMemory memory) {
        println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = Arrays.asList(os.getProcesses(5, ProcessSort.CPU));

        println("   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
        }
    }*/

  /*  private void printSensors(Sensors sensors) {
        println("Sensors:");
        format(" CPU Temperature: %.1f°C%n", sensors.getCpuTemperature());
        println(" Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
        format(" CPU Voltage: %.1fV%n", sensors.getCpuVoltage());
    }
*/
    /*private void printPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder("Power: ");
        if (powerSources.length == 0) {
            sb.append("Unknown");
        } else {
            double timeRemaining = powerSources[0].getTimeRemaining();
            if (timeRemaining < -1d) {
                sb.append("Charging");
            } else if (timeRemaining < 0d) {
                sb.append("Calculating time remaining");
            } else {
                sb.append(String.format("%d:%02d remaining", (int) (timeRemaining / 3600),
                        (int) (timeRemaining / 60) % 60));
            }
        }
        for (PowerSource pSource : powerSources) {
            sb.append(String.format("%n %s @ %.1f%%", pSource.getName(), pSource.getRemainingCapacity() * 100d));
        }
        println(sb.toString());
    }
*/
    private void printDisks(List<HWDiskStore> diskStores) {
        println("**********************************************************");
        println("Disks:");
        for (HWDiskStore disk : diskStores) {
            boolean readwrite = disk.getReads() > 0 || disk.getWrites() > 0;
            format(" %s: (model: %s - S/N: %s) size: %s, reads: %s (%s), writes: %s (%s), xfer: %s ms%n",
                    disk.getName(), disk.getModel(), disk.getSerial(),
                    disk.getSize() > 0 ? FormatUtil.formatBytesDecimal(disk.getSize()) : "?",
                    readwrite ? disk.getReads() : "?", readwrite ? FormatUtil.formatBytes(disk.getReadBytes()) : "?",
                    readwrite ? disk.getWrites() : "?", readwrite ? FormatUtil.formatBytes(disk.getWriteBytes()) : "?",
                    readwrite ? disk.getTransferTime() : "?");
            List<HWPartition> partitions = disk.getPartitions();
            if (partitions == null) {
                // TODO Remove when all OS's implemented
                continue;
            }
            for (HWPartition part : partitions) {
                format(" |-- %s: %s (%s) Maj:Min=%d:%d, size: %s%s%n", part.getIdentification(),
                        part.getName(), part.getType(), part.getMajor(), part.getMinor(),
                        FormatUtil.formatBytesDecimal(part.getSize()),
                        part.getMountPoint().isEmpty() ? "" : " @ " + part.getMountPoint());
            }
        }
    }
/*
    private void printFileSystem(FileSystem fileSystem) {
        println("File System:");

        format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors());

        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            format(
                    " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s%n",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());
        }
    }*/

   /* private void printNetworkInterfaces(NetworkIF[] networkIFs) {
        println("Network interfaces:");
        for (NetworkIF net : networkIFs) {
            format(" Name: %s (%s)%n", net.getName(), net.getDisplayName());
            format("   MAC Address: %s %n", net.getMacaddr());
            format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
            format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr()));
            format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr()));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            format("   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : "");
        }
    }
*/
    /*private void printNetworkParameters(NetworkParams networkParams) {
        println("Network parameters:");
        format(" Host name: %s%n", networkParams.getHostName());
        format(" Domain name: %s%n", networkParams.getDomainName());
        format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers()));
        format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway());
        format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway());
    }*/

    /*private void printDisplays(Display[] displays) {
        println("Displays:");
        int i = 0;
        for (Display display : displays) {
            println(" Display " + i + ":");
            println(display.toString());
            i++;
        }
    }

    private void printUsbDevices(UsbDevice[] usbDevices) {
        println("USB Devices:");
        for (UsbDevice usbDevice : usbDevices) {
            println(usbDevice.toString());
        }
    }*/


}
