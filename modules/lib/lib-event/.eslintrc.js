module.exports = {
    // TODO: Replace all with 'plugin:@enonic/eslint-recommended'
    'extends': [
        'eslint:recommended',
        'plugin:@typescript-eslint/eslint-recommended',
    ],
    'parser': '@typescript-eslint/parser',
    'overrides': [{
        'files': ['*.ts'],
        'extends': [
            'plugin:@typescript-eslint/recommended',
            'plugin:@typescript-eslint/recommended-requiring-type-checking',
        ],

        'parserOptions': {
            'ecmaVersion': 2021,
            'project': 'tsconfig.json',
            tsconfigRootDir: __dirname,
        },
        'rules': {
            'indent': 'off',
            '@typescript-eslint/indent': ['error', 4],
            'semi': 'off',
            'no-unused-vars': 'off',
            '@typescript-eslint/no-unused-vars': ['off'],
            'quotes': 'off',
            '@typescript-eslint/quotes': ['error', 'single', {'avoidEscape': true}],
            '@typescript-eslint/semi': ['error'],
            '@typescript-eslint/no-use-before-define': ['error', {'functions': false, 'classes': true}],
            '@typescript-eslint/member-ordering': ['error'],
            '@typescript-eslint/explicit-function-return-type': ['error', {allowExpressions: true}],
            '@typescript-eslint/unbound-method': ['error', {ignoreStatic: true}],
            '@typescript-eslint/no-unsafe-argument': ['off'],
        },
    }, {
        'files': ['*.js'],
        'globals': {
            'require': 'readonly',
            'log': 'readonly',
            '__': 'readonly',
            'app': 'readonly',
            'testInstance': 'readonly',
            'exports': 'writable',
        },
    }, {
        'files': ['*.json'],
        'rules': {
            'quotes': ['error', 'double'],
            'comma-dangle': ['error', 'never'],
        },
    }],
    'parserOptions': {
        'ecmaVersion': 2021,
    },
    'rules': {
        'indent': ['error', 4],
        'block-spacing': ['error', 'always'],
        'space-before-function-paren': ['error', {'anonymous': 'always', 'named': 'never'}],
        'space-in-parens': ['error', 'never'],
        'object-curly-spacing': ['error', 'never'],
        'lines-between-class-members': ['error', 'always', {exceptAfterSingleLine: true}],
        'spaced-comment': ['error', 'always', {'exceptions': ['-', '+']}],
        'arrow-spacing': ['error', {'before': true, 'after': true}],
        'array-bracket-spacing': ['error', 'never'],
        'computed-property-spacing': ['error', 'never'],
        'template-curly-spacing': ['error', 'never'],
        'object-property-newline': ['off', {'allowMultiplePropertiesPerLine': true}],
        'no-plusplus': ['error', {'allowForLoopAfterthoughts': true}],
        'comma-dangle': ['error', 'always-multiline'],
        'quotes': ['error', 'single', {'avoidEscape': true}],
    },
    'ignorePatterns': [
        'package-lock.json',
        'build',
        '*.d.ts',
        'src/main/resources/lib/xp/*.js',
    ],
    'env': {
        'es6': true,
        'browser': true,
        'node': true,
    },
};
