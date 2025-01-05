const { ApiPromise, WsProvider } = require('@polkadot/api');
const fs = require('fs');

// Generate SQL for modules
function generateModuleSql(pallets) {
  let sql = `-- Populate the module table with explicit IDs\nINSERT INTO module (id, name, description) VALUES\n`;
  pallets.forEach((pallet, index) => {
    const moduleName = pallet.name.toString();
    const moduleIndex = moduleName === 'System' ? 0 : pallet.index?.toNumber() || index;
    const moduleDescription = pallet.docs.length > 0
        ? pallet.docs.map(doc => doc.toString()).join(' ')
        : `No description available for ${moduleName} module.`;
    sql += `  (${moduleIndex}, '${moduleName}', '${moduleDescription.replace(/'/g, "''")}'),\n`;
  });
  return sql.trim().slice(0, -1) + ';\n';
}

// Generate SQL for extrinsics
function generateExtrinsicSql(api, pallets) {
  let sql = `-- Populate the function table with explicit IDs\nINSERT INTO function (id, module_id, call_index, name, description) VALUES\n`;
  let extrinsicIdCounter = 0;

  pallets.forEach((pallet) => {
    const moduleName = pallet.name.toString();
    const moduleIndex = moduleName === 'System' ? 0 : pallet.index?.toNumber();

    if (pallet.calls.isSome) {
      const lookupType = pallet.calls.unwrap().type;
      const callVariants = api.registry.lookup.getSiType(lookupType).def.asVariant.variants;

      callVariants.forEach((variant) => {
        const functionName = variant.name.toString();
        const functionDescription = variant.docs.length > 0
            ? variant.docs.map(doc => doc.toString()).join(' ')
            : `No description available for ${functionName} in ${moduleName}.`;
        const realIndex = variant.index; // Use the real index

        console.log(`Module: ${moduleName}`);
        console.log(`  - ${functionName} (ID: ${realIndex} / Hex: 0x${realIndex.toString(16)})`);
        console.log(`    Description: ${functionDescription}`);

        sql += `  (${extrinsicIdCounter}, ${moduleIndex}, ${realIndex}, '${functionName}', '${functionDescription.replace(/'/g, "''")}'),\n`;
        extrinsicIdCounter++;
      });
    }
  });

  return sql.trim().slice(0, -1) + ';\n';
}

// Write SQL to file
function writeSqlToFile(filepath, sql) {
  fs.writeFileSync(filepath, sql);
  console.log(`SQL file generated: ${filepath}`);
}

// Main function
async function main() {
  const wsProvider = new WsProvider('wss://gdev.coinduf.eu');
  const api = await ApiPromise.create({ provider: wsProvider });

  console.log('--- Generating SQL for modules and extrinsics ---');

  const pallets = api.runtimeMetadata.asLatest.pallets;
  const moduleSql = generateModuleSql(pallets);
  const extrinsicSql = generateExtrinsicSql(api, pallets);

  const sqlOutput = `${moduleSql}\n${extrinsicSql}`;
  writeSqlToFile('../src/main/resources/db/migration/V2__populate_modules_and_functions.sql', sqlOutput);

  process.exit(0);
}

main().catch(console.error);
