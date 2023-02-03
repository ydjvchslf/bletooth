package com.example.bledot.ble

enum class BleState {
    NOT_SCANNED {
        override fun toString(): String {
            return "현재 상태는 [NOT_SCANNED]"
        }
    },
    SCANNING {
        override fun toString(): String {
            return "현재 상태는 [SCANNING]"
        }
    },
    SCAN_COMPLETE_CONNECTED {
        override fun toString(): String {
            return "현재 상태는 [SCAN_COMPLETE_CONNECTED]"
        }
    },
    SCAN_COMPLETE_DISCONNECTED {
        override fun toString(): String {
            return "현재 상태는 [SCAN_COMPLETE_DISCONNECTED]"
        }
    },
    SCAN_COMPLETE_NOT_FOUND {
        override fun toString(): String {
            return "현재 상태는 [SCAN_COMPLETE_NOT_FOUND]"
        }
    },
    CONNECTED {
        override fun toString(): String {
            return "현재 상태는 [CONNECTED]"
        }
    },
    TRYING {
        override fun toString(): String {
            return "현재 상태는 [TRYING]"
        }
    },
    TRYING_TO_DISCONNECT {
        override fun toString(): String {
            return "현재 상태는 [TRYING_TO_DISCONNECT]"
        }
    },
}