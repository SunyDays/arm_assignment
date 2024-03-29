package arm_assignment

import net.fornwall.jelf._
import spray.json._
import arm_assignment.ElfFileStrings._

// Custom JSON serialization/deserialization logic for ElfFile class
object ElfFileJsonProtocol extends DefaultJsonProtocol {
  implicit object ElfFileJsonFormat extends RootJsonFormat[ElfFile] {
    // serialization
    def write(elfFile: ElfFile): JsValue = {
      var sectionHeaders: Array[JsObject] = Array[JsObject]();

      // loop begin from 1 because first section is always NULL
      for (i <- 1 until elfFile.num_sh) {
        val section = elfFile.getSection(i)
        val sectionJson = JsObject(
          "Name"      -> JsString(section.getName()),
          "Type"      -> JsString(getElfSectionTypeStr(section.`type`)),
          "Address"   -> JsNumber(section.`address`),
          "Offset"    -> JsNumber(section.`section_offset`),
          "Size"      -> JsNumber(section.`size`),
          "EntSize"   -> JsNumber(section.`entry_size`),
          "Flags"     -> JsString(getElfSectionFlagsStr(section.`flags`)),
          "Link"      -> JsNumber(section.`link`),
          "Info"      -> JsNumber(section.`info`),
          "Alignment" -> JsNumber(section.`address_alignment`)
        )
        sectionHeaders = sectionHeaders :+ sectionJson
      }

      var programHeaders: Array[JsObject] = Array[JsObject]();
      for (i <- 0 until elfFile.num_ph) {
        val program = elfFile.getProgramHeader(i)
        val programJson = JsObject(
          "Type"     -> JsString(getElfSegmentTypeStr(program.`type`)),
          "Offset"   -> JsNumber(program.`offset`),
          "VirtAddr" -> JsNumber(program.`virtual_address`),
          "PhysAddr" -> JsNumber(program.`physical_address`),
          "FileSiz"  -> JsNumber(program.`file_size`),
          "MemSiz"   -> JsNumber(program.`mem_size`),
          "Flags"    -> JsString(getElfSegmentFlagsStr(program.`flags`)),
          "Align"    -> JsNumber(program.`alignment`)
        )
        programHeaders = programHeaders :+ programJson
      }

      JsObject(
        "Class"                  -> JsString(elfClassStr(elfFile.objectSize)),
        "Encoding"               -> JsString(elfDataStr(elfFile.encoding)),
        "ELF Version"            -> JsNumber(elfFile.elfVersion),
        "ABI"                    -> JsString(elfOsAbiStr(elfFile.abi)),
        "ABI Version"            -> JsNumber(elfFile.abiVersion),
        "File type"              -> JsString(elfTypeStr(elfFile.file_type)),
        "Machine"                -> JsString(elfMachineStr(elfFile.arch)),
        "Version"                -> JsNumber(elfFile.version),
        "Entry point"            -> JsNumber(elfFile.entry_point),
        "Program headers offset" -> JsNumber(elfFile.ph_offset),
        "Section headers offset" -> JsNumber(elfFile.sh_offset),
        "Flags"                  -> JsNumber(elfFile.flags),
        "ELF header size"        -> JsNumber(elfFile.eh_size),
        "Program header size"    -> JsNumber(elfFile.ph_entry_size),
        "Program headers count"  -> JsNumber(elfFile.num_ph),
        "Section header size"    -> JsNumber(elfFile.sh_entry_size),
        "Section headers count"  -> JsNumber(elfFile.num_sh),
        "Section headers"        -> JsArray(sectionHeaders.toVector),
        "Program headers"        -> JsArray(programHeaders.toVector)
      )
    }

    // deserialization stub.
    // Spray.json demands this method, but now it's useless
    def read(value: JsValue): ElfFile = {
      null
    }
  }
}

// Readable values for some ELF fields.
// According to System-V Application Binary Interface, man elf(5) and 'elf.h'
object ElfFileStrings {
  val elfClassStr = Array(
    "Invalid",
    "32-bit",
    "64-bit"
  )

  val elfDataStr = Array(
    "Invalid",
    "LSB",
    "MSB"
  )

  val elfOsAbiStr = Array(
    "System-V",
    "Hewlett-Packard HP-UX",
    "NetBSD",
    "GNU/Linux",
    "reserved",
    "reserved",
    "Sun Solaris",
    "AIX",
    "IRIX",
    "FreeBSD",
    "Compaq TRU64 UNIX",
    "Novell Modesto",
    "Open BSD",
    "Open VMS",
    "Hewlett-Packard Non-Stop Kernel",
    "Amiga Research OS",
    "The FenixOS highly scalable multi-core OS",
    "Nuxi CloudABI",
    "Stratus Technologies OpenVOS"
  )

  val elfMachineStr = Array(
    "No machine",
    "AT&T WE 32100",
    "SPARC",
    "Intel 80386",
    "Motorola 68000",
    "Motorola 88000",
    "Intel MCU",
    "Intel 80860",
    "MIPS I Architecture",
    "IBM System/370 Processor",
    "MIPS RS3000 Little-endian",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "Hewlett-Packard PA-RISC",
    "reserved",
    "Fujitsu VPP500",
    "Enhanced instruction set SPARC",
    "Intel 80960",
    "PowerPC",
    "64-bit PowerPC",
    "IBM System/390 Processor",
    "IBM SPU/SPC",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "NEC V800",
    "Fujitsu FR20",
    "TRW RH-32",
    "Motorola RCE",
    "ARM 32-bit architecture (AARCH32)",
    "Digital Alpha",
    "Hitachi SH",
    "SPARC Version 9",
    "Siemens TriCore embedded processor",
    "Argonaut RISC Core, Argonaut Technologies Inc.",
    "Hitachi H8/300",
    "Hitachi H8/300H",
    "Hitachi H8S",
    "Hitachi H8/500",
    "Intel IA-64 processor architecture",
    "Stanford MIPS-X",
    "Motorola ColdFire",
    "Motorola M68HC12",
    "Fujitsu MMA Multimedia Accelerator",
    "Siemens PCP",
    "Sony nCPU embedded RISC processor",
    "Denso NDR1 microprocessor",
    "Motorola Star*Core processor",
    "Toyota ME16 processor",
    "STMicroelectronics ST100 processor",
    "Advanced Logic Corp. TinyJ embedded processor family",
    "AMD x86-64 architecture",
    "Sony DSP Processor",
    "Digital Equipment Corp. PDP-10",
    "Digital Equipment Corp. PDP-11",
    "Siemens FX66 microcontroller",
    "STMicroelectronics ST9+ 8/16 bit microcontroller",
    "STMicroelectronics ST7 8-bit microcontroller",
    "Motorola MC68HC16 Microcontroller",
    "Motorola MC68HC11 Microcontroller",
    "Motorola MC68HC08 Microcontroller",
    "Motorola MC68HC05 Microcontroller",
    "Silicon Graphics SVx",
    "STMicroelectronics ST19 8-bit microcontroller",
    "Digital VAX",
    "Axis Communications 32-bit embedded processor",
    "Infineon Technologies 32-bit embedded processor",
    "Element 14 64-bit DSP Processor",
    "LSI Logic 16-bit DSP Processor",
    "Donald Knuth's educational 64-bit processor",
    "Harvard University machine-independent object files",
    "SiTera Prism",
    "Atmel AVR 8-bit microcontroller",
    "Fujitsu FR30",
    "Mitsubishi D10V",
    "Mitsubishi D30V",
    "NEC v850",
    "Mitsubishi M32R",
    "Matsushita MN10300",
    "Matsushita MN10200",
    "picoJava",
    "OpenRISC 32-bit embedded processor",
    "ARC International ARCompact processor (old spelling/synonym: ARCH_ARC_A5)",
    "Tensilica Xtensa Architecture",
    "Alphamosaic VideoCore processor",
    "Thompson Multimedia General Purpose Processor",
    "National Semiconductor 32000 series",
    "Tenor Network TPC processor",
    "Trebia SNP 1000 processor",
    "STMicroelectronics (www.st.com) ST200 microcontroller",
    "Ubicom IP2xxx microcontroller family",
    "MAX Processor",
    "National Semiconductor CompactRISC microprocessor",
    "Fujitsu F2MC16",
    "Texas Instruments embedded microcontroller msp430",
    "Analog Devices Blackfin (DSP) processor",
    "S1C33 Family of Seiko Epson processors",
    "Sharp embedded microprocessor",
    "Arca RISC Microprocessor",
    "Microprocessor series from PKU-Unity Ltd. and MPRC of Peking University",
    "eXcess: 16/32/64-bit configurable embedded CPU",
    "Icera Semiconductor Inc. Deep Execution Processor",
    "Altera Nios II soft-core processor",
    "National Semiconductor CompactRISC CRX microprocessor",
    "Motorola XGATE embedded processor",
    "Infineon C16x/XC16x processor",
    "Renesas M16C series microprocessors",
    "Microchip Technology dsPIC30F Digital Signal Controller",
    "Freescale Communication Engine RISC core",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "Renesas M32C series microprocessors",
    "Altium TSK3000 core",
    "Freescale RS08 embedded processor",
    "Analog Devices SHARC family of 32-bit DSP processors",
    "Cyan Technology eCOG2 microprocessor",
    "Sunplus S+core7 RISC processor",
    "New Japan Radio (NJR) 24-bit DSP Processor",
    "Broadcom VideoCore III processor",
    "RISC processor for Lattice FPGA architecture",
    "Seiko Epson C17 family",
    "The Texas Instruments TMS320C6000 DSP family",
    "The Texas Instruments TMS320C2000 DSP family",
    "The Texas Instruments TMS320C55x DSP family",
    "Texas Instruments Application Specific RISC Processor, 32bit fetch",
    "Texas Instruments Programmable Realtime Unit",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "STMicroelectronics 64bit VLIW Data Signal Processor",
    "Cypress M8C microprocessor",
    "Renesas R32C series microprocessors",
    "NXP Semiconductors TriMedia architecture family",
    "QUALCOMM DSP6 Processor",
    "Intel 8051 and variants",
    "STMicroelectronics STxP7x family of configurable and extensible RISC processors",
    "Andes Technology compact code size embedded RISC processor family",
    "Cyan Technology eCOG1X family",
    "Cyan Technology eCOG1X family",
    "Dallas Semiconductor MAXQ30 Core Micro-controllers",
    "New Japan Radio (NJR) 16-bit DSP Processor",
    "M2000 Reconfigurable RISC Microprocessor",
    "Cray Inc. NV2 vector architecture",
    "Renesas RX family",
    "Imagination Technologies META processor architecture",
    "MCST Elbrus general purpose hardware architecture",
    "Cyan Technology eCOG16 family",
    "National Semiconductor CompactRISC CR16 16-bit microprocessor",
    "Freescale Extended Time Processing Unit",
    "Infineon Technologies SLE9X core",
    "Intel L10M",
    "Intel K10M",
    "reserved",
    "ARM 64-bit architecture (AARCH64)",
    "reserved",
    "Atmel Corporation 32-bit microprocessor family",
    "STMicroeletronics STM8 8-bit microcontroller",
    "Tilera TILE64 multicore architecture family",
    "Tilera TILEPro multicore architecture family",
    "Xilinx MicroBlaze 32-bit RISC soft processor core",
    "NVIDIA CUDA architecture",
    "Tilera TILE-Gx multicore architecture family",
    "CloudShield architecture family",
    "KIPO-KAIST Core-A 1st generation processor family",
    "KIPO-KAIST Core-A 2nd generation processor family",
    "Synopsys ARCompact V2",
    "Open8 8-bit RISC soft processor core",
    "Renesas RL78 family",
    "Broadcom VideoCore V processor",
    "Renesas 78KOR family",
    "Freescale 56800EX Digital Signal Controller (DSC)",
    "Beyond BA1 CPU architecture",
    "Beyond BA2 CPU architecture",
    "XMOS xCORE processor family",
    "Microchip 8-bit PIC(r) family",
    "reserved by Intel",
    "reserved by Intel",
    "reserved by Intel",
    "reserved by Intel",
    "reserved by Intel",
    "KM211 KM32 32-bit processor",
    "KM211 KMX32 32-bit processor",
    "KM211 KMX16 16-bit processor",
    "KM211 KMX8 8-bit processor",
    "KM211 KVARC processor",
    "Paneve CDP architecture family",
    "Cognitive Smart Memory Processor",
    "Bluechip Systems CoolEngine",
    "Nanoradio Optimized RISC",
    "CSR Kalimba architecture family",
    "Zilog Z80",
    "Controls and Data Services VISIUMcore processor",
    "FTDI Chip FT32 high performance 32-bit RISC architecture",
    "Moxie processor family",
    "AMD GPU architecture",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "reserved",
    "RISC-V"
  )

  val elfTypeStr = Array(
    "No file type",
    "Relocatable file",
    "Executable file",
    "Shared object file",
    "Core file"
  )

  private val elfSectionTypeStr = Array(
    "NULL",
    "PROGBITS",
    "SYMTAB",
    "STRTAB",
    "RELA",
    "HASH",
    "DYNAMIC",
    "NOTE",
    "NOBITS",
    "REL",
    "SHLIB",
    "DYNSYM",
    "UNKNOWN",
    "UNKNOWN",
    "INIT_ARRAY",
    "FINI_ARRAY",
    "PREINIT_ARRAY",
    "GROUP",
    "SYMTAB_SHNDX"
  )

  // map only main section types, return 'reserved' for others
  def getElfSectionTypeStr(idx: Long): String = {
    if (idx < elfSectionTypeStr.length) {
      elfSectionTypeStr(idx.toInt)
    } else {
      "reserved"
    }
  }

  private val elfSegmentTypeStr = Array(
    "NULL",
    "LOAD",
    "DYNAMIC",
    "INTERP",
    "NOTE",
    "SHLIB",
    "PHDR",
    "TLS"
  )

  // map main segmen types
  def getElfSegmentTypeStr(idx: Long): String = {
    if (idx < elfSegmentTypeStr.length) {
      elfSegmentTypeStr(idx.toInt)
    } else {
      "reserved"
    }
  }

  private val elfSectionFlagStr = Array(
    "WRITE", "ALLOC", "EXECINSTR", "MERGE",
    "STRINGS", "INFO_LINK", "LINK_ORDER",
    "OS_NONCONFORMING", "GROUP", "TLS"
  )

  private val elfSectionFlag = Array(
    0x1, 0x2, 0x4, 0x10, 0x20, 0x40,
    0x80, 0x100, 0x200, 0x400
  )

  def getElfSectionFlagsStr(flags: Long): String = {
    var str = ""

    for (i <- 0 until elfSectionFlag.length) {
      if ((flags & elfSectionFlag(i)) != 0) {
        str = str +
        (if (str.isEmpty) "" else " | ") +
        elfSectionFlagStr(i)
      }
    }

    str
  }

  private val elfSegmentFlagStr = Array(
    "Executable", "Writable", "Readable"
  )

  private val elfSegmentFlag = Array(
    0x1, 0x2, 0x4
  )

  def getElfSegmentFlagsStr(flags: Long): String = {
    var str = ""

    for (i <- 0 until elfSegmentFlag.length) {
      if ((flags & elfSegmentFlag(i)) != 0) {
        str = str +
        (if (str.isEmpty) "" else " | ") +
        elfSegmentFlagStr(i)
      }
    }

    str
  }
}
