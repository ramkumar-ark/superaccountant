package com.superaccountant.ETL;

import com.superaccountant.ETL.model.LedgerMasterXml;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
class LedgerMasterMapper {

    private static final Set<String> BASE_GROUP_NAMES = Set.of(
            "Reserves & Surplus",
            "Secured Loans",
            "Unsecured Loans",
            "Duties & Taxes",
            "Provisions",
            "Sundry Creditors",
            "Stock-in-Hand",
            "Sundry Debtors",
            "Bank OD A/c",
            "Deposits (Asset)",
            "Loans & Advances (Asset)",
            "Bank OCC A/c",
            "Suspense Account");

    public LedgerMaster toLedgerMaster(LedgerMasterXml xml, Map<String, GroupMaster> groupMap) {
        if (xml == null) {
            return null;
        }
        LedgerMaster master = new LedgerMaster();
        master.setName(xml.getName());
        master.setGuid(xml.getGuid());
        master.setGroupName(xml.getGroupName());
        master.setIsGstApplicable("Yes".equalsIgnoreCase(xml.getIsGstApplicable()));
        master.setIsTdsApplicable("Yes".equalsIgnoreCase(xml.getIsTdsApplicable()));
        master.setPan(xml.getPan());
        master.setGstin(xml.getGstin());
        master.setTypeOfSupply(xml.getTypeOfSupply());

        // Find and set the base group name by traversing the hierarchy in memory
        master.setBaseGroupName(findBaseGroupName(xml.getGroupName(), groupMap));

        return master;
    }

    private String findBaseGroupName(String groupName, Map<String, GroupMaster> groupMap) {
        if (groupName == null || groupMap == null || groupMap.isEmpty()) {
            return groupName;
        }
        GroupMaster currentGroup = groupMap.get(groupName);
        if (currentGroup == null) {
            return groupName; // Fallback if the group itself is not in the map
        }

        while (currentGroup.getParent() != null) {
            if (BASE_GROUP_NAMES.contains(currentGroup.getName())) {
                break; // Stop traversing if we hit a defined base group.
            }
            currentGroup = currentGroup.getParent();
        }
        return currentGroup.getName();
    }
}