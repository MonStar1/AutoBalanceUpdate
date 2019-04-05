package com.balance.update.autobalanceupdate.presentation.entities

import com.balance.update.autobalanceupdate.data.db.entities.Filter
import com.balance.update.autobalanceupdate.data.db.entities.UnresolvedSms

data class UnresolvedSmsCard(val unresolvedSms: UnresolvedSms, val filters: List<Filter>)